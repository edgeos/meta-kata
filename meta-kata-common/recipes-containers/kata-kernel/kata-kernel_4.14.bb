# linux-yocto-custom.bb:
#
#   An example kernel recipe that uses the linux-yocto and oe-core
#   kernel classes to apply a subset of yocto kernel management to git
#   managed kernel repositories.
#
#   To use linux-yocto-custom in your layer, copy this recipe (optionally
#   rename it as well) and modify it appropriately for your machine. i.e.:
#
#     COMPATIBLE_MACHINE_yourmachine = "yourmachine"
#
#   You must also provide a Linux kernel configuration. The most direct
#   method is to copy your .config to files/defconfig in your layer,
#   in the same directory as the copy (and rename) of this recipe and
#   add file://defconfig to your SRC_URI.
#
#   To use the yocto kernel tooling to generate a BSP configuration
#   using modular configuration fragments, see the yocto-bsp and
#   yocto-kernel tools documentation.
#
# Warning:
#
#   Building this example without providing a defconfig or BSP
#   configuration will result in build or boot errors. This is not a
#   bug.
#
#
# Notes:
#
#   patches: patches can be merged into to the source git tree itself,
#            added via the SRC_URI, or controlled via a BSP
#            configuration.
#
#   defconfig: When a defconfig is provided, the linux-yocto configuration
#              uses the filename as a trigger to use a 'allnoconfig' baseline
#              before merging the defconfig into the build. 
#
#              If the defconfig file was created with make_savedefconfig, 
#              not all options are specified, and should be restored with their
#              defaults, not set to 'n'. To properly expand a defconfig like
#              this, specify: KCONFIG_MODE="--alldefconfig" in the kernel
#              recipe.
#   
#   example configuration addition:
#            SRC_URI += "file://smp.cfg"
#   example patch addition (for kernel v4.x only):
#            SRC_URI += "file://0001-linux-version-tweak.patch"
#   example feature addition (for kernel v4.x only):
#            SRC_URI += "file://feature.scc"
#
SUMMARY = "This recipe builds the Linux guest-VM's kernel for katacontainers"
SECTION = "Kata VM Linux Kernel"
LICENSE = "GPL-2.0"
PROVIDES = "kata-kernel"
RPROVIDES_{PN} = "kata-kernel"

inherit autotools 

DEPENDS += " \
    xz-native \
    bc-native \
    elfutils-native \
"

LINUX_MAJOR_REV = "4"
LINUX_MINOR_REV = "14"
LINUX_PATCH_REV = "22" 
LINUX_VERSION = "${LINUX_MAJOR_REV}.${LINUX_MINOR_REV}.${LINUX_PATCH_REV}"
KERNEL_FILE = "linux-${LINUX_VERSION}"
KERNEL_TAR_FILE = "${KERNEL_FILE}.tar.xz"

addtask kata_arch_check before do_fetch
do_kata_arch_check(){
    case "${TARGET_ARCH}" in
        x86_64)
            bbnote "Supported architecture detected: ${TARGET_ARCH}"
        ;;

        aarch64|ppc64le)
             bbwarn "Untested Kata architecture: ${TARGET_ARCH}"
             bbfatal "Recipe incomplete for this architecture: ${TARGET_ARCH}"
        ;;

        *)
             bbfatal "Unsupported Kata architecture: ${TARGET_ARCH}"
    esac
}

FILESEXTRAPATHS_append := ":${THISDIR}/files"
SRC_URI = " https://cdn.kernel.org/pub/linux/kernel/v${LINUX_MAJOR_REV}.x/${KERNEL_TAR_FILE};name=kernel "
SRC_URI_append_aarch64 = " https://raw.githubusercontent.com/kata-containers/packaging/master/kernel/configs/arm64_kata_kvm_4.14.x;name=defconfig;downloadfilename=defconfig "
SRC_URI_append_x86-64 = " https://raw.githubusercontent.com/kata-containers/packaging/3ff5b41e5874d796acd956e56101f27dcc0e9556/kernel/configs/x86_64_kata_kvm_4.14.x;name=defconfig;downloadfilename=defconfig; "

CONF_MD5SUM_aarch64 = "6859abd6265a1f44e6d92aaa8dbd2f45"
CONF_MD5SUM_x86-64 = "00b8d6c39e2bb66c012b26a64b24814b"

CONF_SHA256SUM_aarch64 = "440a5f60623f0736b79b1b5ade2b6c7fa9c8a08821ee26c3ba2922af0b187efd"
CONF_SHA256SUM_x86-64 = "60507ca2c0ced1b96dd2086527c0cca41e478c83784d184d3b726202e76d4ecc"

KERNEL_MD5SUM = "0c033c0420ae28017c2f15e02d0cf90c"

KERNEL_SHA256SUM = "4ce2fa81854971c2f50462e14e790885ab13376870c41dd5208ccd7619617fbe"

SRC_URI[defconfig.md5sum] = "${CONF_MD5SUM}"
SRC_URI[defconfig.sha256sum] = "${CONF_SHA256SUM}"

SRC_URI[kernel.md5sum] = "${KERNEL_MD5SUM}"
SRC_URI[kernel.sha256sum] = "${KERNEL_SHA256SUM}"


S = "${WORKDIR}/${KERNEL_FILE}"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

# Override COMPATIBLE_MACHINE to include your machine in a copy of this recipe
# file. Leaving it empty here ensures an early explicit build failure.
# COMPATIBLE_MACHINE = "(qemux86|qemux86-64)"



do_patch(){
    cd ${S}
    # patch -p1 ${WORKDIR}/0001-NO-UPSTREAM-9P-always-use-cached-inode-to-fill-in-v9
    curl -L https://raw.githubusercontent.com/kata-containers/packaging/master/kernel/patches/0001-NO-UPSTREAM-9P-always-use-cached-inode-to-fill-in-v9.patch | patch -p1
}

do_configure(){
    cp ${WORKDIR}/defconfig ${S}/.config
}

EXTRA_OEMAKE = " HOSTCC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}" HOSTCPP="${BUILD_CPP}""
do_compile(){
    cd ${S}
    oe_runmake
}


KERNEL_ARCH_x86-64 = "x86_64"
KERNEL_ARCH_aarch64 = "arm64"
do_install(){
    kata_kernel_dir="${D}/usr/share/kata-containers"
    kata_kernel_bin="kata-vmlinuz-${LINUX_VERSION}.container"
    kata_vmlinuz="${kata_kernel_dir}/${kata_kernel_bin}"
    case ${KERNEL_ARCH} in
        ppc64le)
             kernel_file="$(realpath ./vmlinux)"
        ;;
        x86_64)
             kernel_file="${S}/arch/${KERNEL_ARCH}/boot/bzImage"
        ;;
        arm64)
             kernel_file="${S}/arch/${KERNEL_ARCH}/boot/Image.gz"
        ;;
    esac
    install -o root -g root -m 0755 -D "${kernel_file}" "${kata_vmlinuz}"
    (cd ${kata_kernel_dir}; ln -sf "${kata_kernel_bin}" "vmlinuz.container")

    kata_vmlinux_bin="kata-vmlinux-${LINUX_VERSION}"
    kata_vmlinux="${kata_kernel_dir}/${kata_vmlinux_bin}"
    install -o root -g root -m 0755 -D "${S}/vmlinux" "${kata_vmlinux}"
    (cd ${kata_kernel_dir}; ln -sf "${kata_vmlinux_bin}" "vmlinux.container")

    chown root:root -R ${D}
}

FILES_${PN} += " \
    /usr/share/kata-containers/kata-vmlinuz-4.14.22.container \
    /usr/share/kata-containers/vmlinux.container \
    /usr/share/kata-containers/kata-vmlinux-4.14.22 \
    /usr/share/kata-containers/vmlinuz.container \
"

SUMMARY = "This recipe installs QEMU-lite as configured for katacontainers"
SECTION = "Install Kata-Qemu"
PROVIDES = "kata-qemu"
RPROVIDES_{PN} = "kata-qemu"
LICENSE = "Apache-2.0 & GPL-2.0 & BSD"

inherit pkgconfig 

SRC_URI = " \
        git://github.com/kata-containers/qemu;protocol=https;branch=qemu-lite-2.11.0;rev=b0fbe46ad82982b289a44ee2495b59b0bad8a842;destsuffix=git/qemu \
        git://github.com/kata-containers/packaging;protocol=https;rev=0f5e37cf9fad7527bcf56439f3f19adb38930579;destsuffix=git/packaging \
        git://git.qemu.org/git/capstone.git/;protocol=https;rev=22ead3e0bfdb87516656453336160e0a37b066bf;destsuffix=git/qemu/capstone \
        git://git.qemu.org/git/keycodemapdb.git/;protocol=https;rev=10739aa26051a5d49d88132604539d3ed085e72e;destsuffix=git/qemu/ui/keycodemapdb \
        git://git.qemu.org/git/dtc.git/;protocol=https;rev=558cd81bdd432769b59bff01240c44f82cfb1a9d;destsuffix=git/qemu/dtc \
        "

LIC_FILES_CHKSUM = " \
        file://qemu/LICENSE;md5=785f77b71965576e3a82281a9bd474c5 \
        file://packaging/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
        file://qemu/capstone/LICENSE.TXT;md5=1cfbff4f40612b0144e498a47c91499c \
        file://qemu/ui/keycodemapdb/LICENSE.BSD;md5=5ae30ba4123bc4f2fa49aa0b0dce887b \
        file://qemu/ui/keycodemapdb/LICENSE.GPL2;md5=751419260aa954499f7abaabaa882bbe \
        file://qemu/dtc/GPL;md5=94d55d512a9ba36caa9b7df079bae19f \
        "

DEPENDS += " \
    libcap-ng \
    glib-2.0 \
    pixman \
    libcap \
    zlib \ 
    glibc \
    libgcc \
"
DEPENDS_kata-withceph += " ceph "

RDEPENDS_{PN} += " \
    libcap-ng \
    glib-2.0 \
    pixman \
    libcap \
    zlib \ 
    glibc \
    libgcc \
"

S = "${WORKDIR}/git"

do_configure() {
    sed -i \
        -e "s,\$(arch),${TARGET_ARCH},g" \
        ${S}/packaging/scripts/configure-hypervisor.sh

    cd ${S}/qemu
    rm -rf build 
    mkdir build
    cd build

    confoptions=" --disable-git-update "
    if ${@"true" if 'kata-withceph' in d.getVar('OVERRIDES', True) else "false"}; then
        confoptions="${confoptions} --enable-rbd "
    fi
    if ${@"true" if 'x86-64' in d.getVar('OVERRIDES', True) else "false"}; then
        confoptions="${confoptions} --target-list=x86_64-softmmu "
    fi
    if ${@"true" if 'aarch64' in d.getVar('OVERRIDES', True) else "false"}; then
        confoptions="${confoptions} --target-list=aarch64-softmmu "
    fi

    ../configure $(${S}/packaging/scripts/configure-hypervisor.sh qemu-lite | sed 's,\-\-enable\-rbd,,') ${confoptions}

    if ${@"true" if 'x86-64' in d.getVar('OVERRIDES', True) else "false"}; then
        populate_git_submodule_file ui/keycodemapdb capstone dtc
    fi
    if ${@"true" if 'aarch64' in d.getVar('OVERRIDES', True) else "false"}; then
        populate_git_submodule_file ui/keycodemapdb capstone dtc
    fi
}

do_compile() {
   cd ${S}/qemu/build
   oe_runmake
}

do_install() {
    cd ${S}/qemu/build && make install DESTDIR="${D}"
    mkdir -p ${D}/usr/bin
    (cd ${D}/usr/bin; ln -s ../local/bin/qemu-system-${TARGET_ARCH} qemu-lite-system-${TARGET_ARCH})
    prune_install
    chown root:root -R "${D}"
}

INSANE_SKIP_${PN} = "already-stripped"

FILES_${PN} += " \
    /usr \
    /usr/bin \
"
FILES_${PN}_append_x86-64 = " /usr/bin/qemu-lite-system-x86_64 /usr/local/bin/qemu-system-x86_64 "
FILES_${PN}_append_aarch64 = " /usr/bin/qemu-lite-system-aarch64 /usr/local/bin/qemu-system-aarch64 "


populate_git_submodule_file(){
    # Adapted from scripts/git-submodule.sh
    substat=".git-submodule-status"
    modules="$@"

    cd ${S}/qemu
    if test -z "modules"
    then
        test -e $substat || touch $substat
        bbwarn "No submodules"
    else
        git submodule status $modules > "${substat}"
        test $? -ne 0 && error "failed to save git submodule status" >&2
    fi
    cd -
}

prune_install(){
    rm ${D}/usr/local/share/qemu/s390-netboot.img
    rm ${D}/usr/local/share/qemu/openbios-sparc64
    rm ${D}/usr/local/share/qemu/s390-ccw.img
    rm ${D}/usr/local/share/qemu/openbios-sparc32
    rm ${D}/usr/local/share/qemu/openbios-ppc
    rm ${D}/usr/local/share/qemu/u-boot.e500
    rm ${D}/usr/local/share/qemu/palcode-clipper
}

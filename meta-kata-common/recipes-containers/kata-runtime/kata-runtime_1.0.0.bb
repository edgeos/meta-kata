SUMMARY = "This recipe installs Kata-Runtime for katacontainers as a docker runtime"
SECTION = "Install Kata-Containers"
LICENSE = "Apache-2.0"
PROVIDES = "kata-runtime"
RPROVIDES_{PN} = "kata-runtime"

GO_IMPORT = "github.com/kata-containers/runtime"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI = " \
    git://${GO_IMPORT};protocol=https;destsuffix=${PN}-${PV}/src/${GO_IMPORT} \
"
SRCREV = "cd514b69fd04294a55fa640e4812c1393e747042"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

DEPENDS += " \
    docker \
"

RDEPENDS_{PN} += " \
    kata-proxy \
    kata-shim \
    kata-osbuilder \
    kata-qemu \
     \
    docker \
"

inherit go
inherit goarch

do_compile() {
    # Go variables for Bitbake build-system integration
    export GOPATH="${S}:${STAGING_DIR_TARGET}/${prefix}/local/go"

    cd ${S}/src/${GO_IMPORT}
    make 
}

do_install() {
    # Go variables for Bitbake build-system integration
    export GOPATH="${S}:${STAGING_DIR_TARGET}/${prefix}/local/go"

    cd ${S}/src/${GO_IMPORT}

    makefile_config_file_patch
    make install DESTDIR=${D}/ #DESTBINDIR="${D}/usr/local/bin" SCRIPTS_DIR="${D}/\$(BINDIR)" 

    config_file_mods

    chown root:root -R ${D}
}


FILES_${PN} += " \
  /usr/share \
  /usr/share/defaults \
  /usr/share/defaults/kata-containers \
  /usr/share/defaults/kata-containers/configuration.toml \
  /usr/local/bin/kata-runtime \
  /usr/local/bin/kata-collect-data.sh \
  "

#######################
## Utility Functions ##
#######################

makefile_config_file_patch(){
    sed -i \
    -e "s,\$(QUIET_INST)install -D \$(CONFIG) \$(DESTCONFIG),\$(QUIET_INST)install -D \$(CONFIG) ${D}/\$(DESTCONFIG)," \
    Makefile
}

config_file_mods(){
	bboverride_selections

	if ${@"true" if 'aarch64' in d.getVar('OVERRIDES', True) else "false"}; then
		sed -i 's/^#default_memory = 2048/default_memory = 256/' ${D}/usr/share/defaults/kata-containers/configuration.toml
	fi
}

bboverride_selections(){

    # Use BBOVERRIDES to select initrd/rootfs as image. Select only one. initrd is default.
    if ${@"true" if 'kata-rootfs' in d.getVar('OVERRIDES', True) else "false"} && \
            ${@"true" if 'kata-initrd' in d.getVar('OVERRIDES', True) else "false"}; then
        bbfatal "Can only select one of: kata-rootfs, kata-initrd (Default)"
    elif ${@"true" if 'kata-initrd' in d.getVar('OVERRIDES', True) else "false"}; then
        sed -i 's/^\(image =.*\)/# \1/g' ${D}/usr/share/defaults/kata-containers/configuration.toml
    elif ${@"true" if 'kata-rootfs' in d.getVar('OVERRIDES', True) else "false"}; then
        sed -i 's/^\(initrd =.*\)/# \1/g' ${D}/usr/share/defaults/kata-containers/configuration.toml
    else
        sed -i 's/^\(image =.*\)/# \1/g' ${D}/usr/share/defaults/kata-containers/configuration.toml
    fi


    # Use BBOVERRIDES to select internetworking model. Select only one. Factory default is macvtap, but this isn't working yet.
    # Recipe default is bridged.
    if ${@"true" if 'kata-network-bridged' in d.getVar('OVERRIDES', True) else "false"} && \
            ${@"true" if 'kata-network-macvtap' in d.getVar('OVERRIDES', True) else "false"}; then
        bbfatal "Can only select one of: kata-network-bridged (Recipe Default), kata-network-macvtap (Factory Default, not working)"
    elif ${@"true" if 'kata-network-bridged' in d.getVar('OVERRIDES', True) else "false"}; then
        sed -i 's/^internetworking_model="macvtap"/internetworking_model="bridged"/' ${D}/usr/share/defaults/kata-containers/configuration.toml
    elif ${@"true" if 'kata-network-macvtap' in d.getVar('OVERRIDES', True) else "false"}; then
        echo "macvtap internetwork-model is factory default"
    else
        sed -i 's/^internetworking_model="macvtap"/internetworking_model="bridged"/' ${D}/usr/share/defaults/kata-containers/configuration.toml
    fi

}

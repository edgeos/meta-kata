SUMMARY = "This recipe installs Kata-Shim for katacontainers as a docker runtime"
SECTION = "Install Kata-Proxy"
LICENSE = "Apache-2.0"
PROVIDES = "kata-shim"
RPROVIDES_{PN} = "kata-shim"

GO_IMPORT = "github.com/kata-containers/shim"

SRC_URI = "git://${GO_IMPORT};protocol=https;destsuffix=${PN}-${PV}/src/${GO_IMPORT}"
SRCREV = "0a37760c0224167143cb3cc920c78f5147f52e70"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

DEPENDS += " \
    kata-runtime \
    kata-proxy \
"

RDEPENDS_{PN} += " \
    kata-runtime \
    kata-proxy \
    kata-osbuilder \
    kata-qemu \
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
    export GOPATH="${S}:${STAGING_DIR_TARGET}/${prefix}/local/go"

    cd ${S}/src/${GO_IMPORT}
    install -d ${D}/usr/libexec/kata-containers
    make install PREFIX="${D}/usr"

    chown root:root -R ${D}
}

FILES_${PN} += " \
   /usr/libexec/kata-containers/kata-shim \
   "

SUMMARY = "This recipe installs Kata-Proxy for katacontainers as a docker runtime"
SECTION = "Install Kata-Proxy"
LICENSE = "Apache-2.0"
PROVIDES = "kata-proxy"
RPROVIDES_{PN} = "kata-proxy"

GO_IMPORT = "github.com/kata-containers/proxy"

SRC_URI = "git://${GO_IMPORT};protocol=https;destsuffix=${PN}-${PV}/src/${GO_IMPORT}"
#SRCREV = "a69326b63802952b14203ea9c1533d4edb8c1d64"
SRCREV = "6f209b7f3c586c5f17b52df8bd5d6edbc45ee477"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

DEPENDS += " \
    kata-runtime \
"

RDEPENDS_{PN} += " \
    kata-runtime \
    kata-shim \
    kata-osbuilder \
    kata-qemu \
"

inherit go
inherit goarch

do_compile() {
    export GOPATH="${S}:${STAGING_DIR_TARGET}/${prefix}/local/go"
    export CGO_ENABLED="0"
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
   /usr/libexec/kata-containers/kata-proxy \
   "

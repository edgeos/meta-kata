SUMMARY = "Edgeos Package Group for Containers"
LICENSE = "MIT"

PR = "r1"

inherit packagegroup

RDEPENDS_${PN}_append = " \
    kata-runtime \
    kata-proxy \
    kata-shim \
    kata-osbuilder \
    kata-kernel \
    kata-qemu \
"

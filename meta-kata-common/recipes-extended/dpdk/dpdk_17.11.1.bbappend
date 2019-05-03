SRC_URI_remove = " \
                  git://dpdk.org/dpdk${STABLE};branch=${BRANCH} \
                 "
SRC_URI_append = "https://github.com/spdk/dpdk/archive/6ece49ad5a26f5e2f5c4af6c06c30376c0ddc387.zip"
SRC_URI[md5sum] = "d28cb963831dc09b341eb6ec93ffc42c"
S = "${WORKDIR}/dpdk-6ece49ad5a26f5e2f5c4af6c06c30376c0ddc387"
STABLE = ""

COMPATIBLE_MACHINE_qemux86-64 = "qemux86-64"

do_compile () {
        unset LDFLAGS TARGET_LDFLAGS BUILD_LDFLAGS

        cd ${S}/${RTE_TARGET}
        oe_runmake EXTRA_LDFLAGS="-L${STAGING_LIBDIR} --hash-style=gnu" \
                   EXTRA_CFLAGS="--sysroot=${STAGING_DIR_HOST} -I${STAGING_INCDIR} -fPIC" \
                   CROSS="${TARGET_PREFIX}" \
                   prefix=""  LDFLAGS=""  WERROR_FLAGS="-w" V=1

        cd ${S}/examples/
        oe_runmake EXTRA_LDFLAGS="-L${STAGING_LIBDIR} --hash-style=gnu -fuse-ld=bfd" \
                   EXTRA_CFLAGS="--sysroot=${STAGING_DIR_HOST} -I${STAGING_INCDIR}" \
                   CROSS="${TARGET_PREFIX}" O="${S}/examples/$@/"

        cd ${S}/test/
        oe_runmake EXTRA_LDFLAGS="-L${STAGING_LIBDIR} --hash-style=gnu -fuse-ld=bfd" \
                   EXTRA_CFLAGS="--sysroot=${STAGING_DIR_HOST} -I${STAGING_INCDIR}" \
                   CROSS="${TARGET_PREFIX}" O="${S}/test/$@/"
}


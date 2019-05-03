SUMMARY = "Storage Performance Development Kit"
SECTION = "Install SPDK"
LICENSE = "BSD"

SRC_URI = " \
           git://github.com/ceph/spdk.git;protocol=https;branch=master \
           file://Makefile.patch \
          "

SRCREV = "b17d4d91359078a00ec60e6016b8c5c9c6b41a91"

LIC_FILES_CHKSUM = " \
                    file://LICENSE;md5=74c8615b26bb2e6f98af4ff4919c99ed \
                    "

DEPENDS += " \
    dpdk \
    util-linux \
    cunit \
    openssl \
    libaio \
    "

S = "${WORKDIR}/git"

do_configure() {
  cd ${S}
  #Create a dpdk dir from sysroot that matches cephs expectation of dpdk
  DPDK_DIR=${WORKDIR}/dpdk
  rm -rf ${DPDK_DIR}
  mkdir -p ${DPDK_DIR}/lib
  mkdir -p ${DPDK_DIR}/include
  cd ${DPDK_DIR}/lib
  for f in `ls ${STAGING_DIR_TARGET}/usr/lib/librte*`; do
    ln -s $f
  done
  cd ${DPDK_DIR}/include
  for f in `ls ${STAGING_DIR_TARGET}/usr/include/rte*`; do
    ln -s $f
  done

  cd ${S}
  ./configure --with-dpdk=${DPDK_DIR}
}

do_compile_prepend() {
  #SPDK's Makefiles extensively use "VAR +=" which will be overwritten if we pass in CFLAGS/CXXFLAGS/LDFLAGS thru environment
  #  To work around pass CFLAGS/CXXFLAGS/LDFLAGS in as part of CC/CXX variables and unset CFLAGS/CXXFLAGS/LDFLAGS.
  YOCTO_CFLAGS=$CFLAGS
  YOCTO_CXXFLAGS=$CXXFLAGS
  YOCTO_LDFLAGS=$LDFLAGS

  YOCTO_CC=$CC
  YOCTO_CXX=$CXX

  unset CFLAGS
  unset CXXFLAGS
  unset LDFLAGS

  export CC="$YOCTO_CC $YOCTO_CFLAGS $YOCTO_LDFLAGS"
  export CXX="$YOCTO_CXX $YOCTO_CXXFLAGS $YOCTO_LDFLAGS"
}

do_install() {
  install -d ${D}${libdir}
  install -m 0775 ${S}/build/lib/* ${D}${libdir}

  install -d ${D}${includedir}/${BPN}
  install -m 0775 ${S}/include/${BPN}/* ${D}${includedir}/${BPN}
}

FILES_${PN} = " \
               ${libdir}/* \
               ${includedir}/${BPN}/* \
              "

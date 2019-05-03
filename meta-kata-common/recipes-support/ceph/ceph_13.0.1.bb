SUMMARY = "Ceph - a scalable distributed storage system"
SECTION = "Install Ceph"
PROVIDES = "ceph"
RPROVIDES_{PN} = "ceph"
LICENSE = "LGPL-2.1 & GPL-2.0"

inherit setuptools cmake

SRC_URI = " \
           git://github.com/ceph/ceph.git;protocol=https;branch=master;rev=9cce2427765ce9f869aef281468fd0d36de2dd79;destsuffix=git/ceph \
           file://CMakeLists.txt.patch \
           file://src-CMakeLists.txt.patch \
           file://src-compressor-zstd-CMakeLists.txt.patch \
           file://Findrocksdb.cmake.patch \
           file://Finddpdk.cmake.patch \
           file://Findspdk.cmake \
          "
# file://BuildSPDK.cmake.patch

LIC_FILES_CHKSUM = " \
                    file://COPYING-GPL2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING-LGPL2.1;md5=fbc093901857fcd118f065f900982c24 \
                    "


#ibverbs
DEPENDS += " \
    coreutils \
    pkgconfig \
    glib-2.0 \
    udev \
    libgudev \
    openldap \
    fuse \
    libaio \
    cunit \
    xfsprogs \
    leveldb-kata \
    snappy \
    lz4 \
    gperftools \
    keyutils \
    nss \
    nspr \
    python-dev \
    oath \
    lttng-ust \
    lttng-tools \
    babeltrace \
    python-cython-native \
    gperf-native \
    boost \
    dpdk \
    spdk \
    virtual/libibverbs \
    systemd \
    rocksdb \
    "
RDEPENDS_{PN} += " \
    coreutils \
    pkgconfig \
    glib-2.0 \
    udev \
    libgudev \
    openldap \
    fuse \
    libaio \
    cunit \
    xfsprogs \
    leveldb-kata \
    snappy \
    lz4 \
    gperftools \
    keyutils \
    nss \
    nspr \
    python-dev \
    oath \
    lttng-ust \
    lttng-tools \
    babeltrace \
    python-cython-native \
    gperf-native \
    boost \
    dpdk \
    spdk \
    virtual/libibverbs \
    systemd \
    rocksdb \
    "

S = "${WORKDIR}/git/ceph"
B = "${WORKDIR}/git/ceph/build"

OECMAKE_AR = "${STAGING_BINDIR_TOOLCHAIN}/${AR}"
EXTRA_OECMAKE = " \
                 -DBOOST_J=${@d.getVar('BB_NUMBER_THREADS', True)} \
                 -DWITH_MANPAGE=OFF \
                 -DWITH_TESTS=OFF \
                 -DWITH_SYSTEM_BOOST=ON \
                 -DWITH_SYSTEM_ROCKSDB=ON \
                 -DBUILD_SHARED_LIBS=OFF \
                 -DCMAKE_SYSROOT=${STAGING_DIR_TARGET} \
                 -LH \
                "


do_configure() {
  #Add ability to find SPDK from sysroot
  cp ${WORKDIR}/Findspdk.cmake ${S}/cmake/modules

  #Get submodules (from do_cmake.sh)
  cd ${S}
  git submodule update --init --recursive
  
  cmake_do_configure

  #minimal config to find plugins (from do_cmake.sh)
  echo 'plugin dir = lib' > ceph.conf
  echo 'erasure code dir = lib' >> ceph.conf

  #give vstart a (hopefully) unique mon port to start with (from do_cmake.sh)
  echo $(expr $RANDOM % 1000 + 40000) > .ceph_port
}

do_install(){
   cd ${B}
   for cm in $(find src -name cmake_install.cmake -type f)
   do
       sed -i "s,/etc,${D},g" ${cm}
   done
   cmake -DCMAKE_SYSROOT=${STAGING_DIR_TARGET} -DCMAKE_INSTALL_PREFIX="${D}/usr" -P cmake_install.cmake

   chown -R root:root ${D}
}

INSANE_SKIP_${PN} = "dev-so"

FILES_${PN} += " \
    /init.d \
    /init.d/ceph \
    /usr \
    /usr/lib \
    /usr/lib/libos_tp.so.1.0.0 \
    /usr/lib/libcephfs.so \
    /usr/lib/librbd.so.1 \
    /usr/lib/libosd_tp.so.1 \
    /usr/lib/libosd_tp.so \
    /usr/lib/librbd_tp.so.1.0.0 \
    /usr/lib/librados.so \
    /usr/lib/librbd_tp.so \
    /usr/lib/libosd_tp.so.1.0.0 \
    /usr/lib/libradosstriper.so.1 \
    /usr/lib/libos_tp.so \
    /usr/lib/librgw.so \
    /usr/lib/librados.so.2.0.0 \
    /usr/lib/librgw.so.2 \
    /usr/lib/librbd.so.1.12.0 \
    /usr/lib/librbd_tp.so.1 \
    /usr/lib/librados.so.2 \
    /usr/lib/libos_tp.so.1 \
    /usr/lib/libcephfs.so.2.0.0 \
    /usr/lib/libradosstriper.so.1.0.0 \
    /usr/lib/librados_tp.so \
    /usr/lib/libradosstriper.so \
    /usr/lib/rados-classes \
    /usr/lib/rados-classes/libcls_lua.so \
    /usr/lib/rados-classes/libcls_cephfs.so.1 \
    /usr/lib/rados-classes/libcls_kvs.so \
    /usr/lib/rados-classes/libcls_journal.so.1.0.0 \
    /usr/lib/rados-classes/libcls_replica_log.so.1 \
    /usr/lib/rados-classes/libcls_rbd.so.1 \
    /usr/lib/rados-classes/libcls_log.so.1.0.0 \
    /usr/lib/rados-classes/libcls_refcount.so \
    /usr/lib/rados-classes/libcls_version.so.1 \
    /usr/lib/rados-classes/libcls_refcount.so.1.0.0 \
    /usr/lib/rados-classes/libcls_hello.so.1 \
    /usr/lib/rados-classes/libcls_journal.so \
    /usr/lib/rados-classes/libcls_version.so.1.0.0 \
    /usr/lib/rados-classes/libcls_kvs.so.1.0.0 \
    /usr/lib/rados-classes/libcls_statelog.so.1.0.0 \
    /usr/lib/rados-classes/libcls_sdk.so.1 \
    /usr/lib/rados-classes/libcls_journal.so.1 \
    /usr/lib/rados-classes/libcls_timeindex.so \
    /usr/lib/rados-classes/libcls_timeindex.so.1 \
    /usr/lib/rados-classes/libcls_cephfs.so \
    /usr/lib/rados-classes/libcls_replica_log.so \
    /usr/lib/rados-classes/libcls_numops.so \
    /usr/lib/rados-classes/libcls_statelog.so \
    /usr/lib/rados-classes/libcls_version.so \
    /usr/lib/rados-classes/libcls_user.so.1 \
    /usr/lib/rados-classes/libcls_kvs.so.1 \
    /usr/lib/rados-classes/libcls_lock.so.1 \
    /usr/lib/rados-classes/libcls_numops.so.1 \
    /usr/lib/rados-classes/libcls_hello.so \
    /usr/lib/rados-classes/libcls_replica_log.so.1.0.0 \
    /usr/lib/rados-classes/libcls_user.so \
    /usr/lib/rados-classes/libcls_lua.so.1.0.0 \
    /usr/lib/rados-classes/libcls_lock.so \
    /usr/lib/rados-classes/libcls_rgw.so.1.0.0 \
    /usr/lib/rados-classes/libcls_statelog.so.1 \
    /usr/lib/rados-classes/libcls_refcount.so.1 \
    /usr/lib/rados-classes/libcls_rbd.so.1.0.0 \
    /usr/lib/rados-classes/libcls_lock.so.1.0.0 \
    /usr/lib/rados-classes/libcls_rgw.so.1 \
    /usr/lib/rados-classes/libcls_sdk.so \
    /usr/lib/rados-classes/libcls_log.so \
    /usr/lib/rados-classes/libcls_timeindex.so.1.0.0 \
    /usr/lib/rados-classes/libcls_rgw.so \
    /usr/lib/rados-classes/libcls_numops.so.1.0.0 \
    /usr/lib/rados-classes/libcls_lua.so.1 \
    /usr/lib/rados-classes/libcls_cephfs.so.1.0.0 \
    /usr/lib/rados-classes/libcls_rbd.so \
    /usr/lib/rados-classes/libcls_log.so.1 \
    /usr/lib/rados-classes/libcls_user.so.1.0.0 \
    /usr/lib/rados-classes/libcls_hello.so.1.0.0 \
    /usr/lib/rados-classes/libcls_sdk.so.1.0.0 \
    /usr/lib/librbd.so \
    /usr/lib/librgw.so.2.0.0 \
    /usr/lib/librados_tp.so.2 \
    /usr/lib/ceph \
    /usr/lib/ceph/libceph-common.so \
    /usr/lib/ceph/erasure-code \
    /usr/lib/ceph/erasure-code/libec_jerasure_sse3.so \
    /usr/lib/ceph/erasure-code/libec_jerasure_generic.so \
    /usr/lib/ceph/erasure-code/libec_shec_generic.so \
    /usr/lib/ceph/erasure-code/libec_jerasure_sse4.so \
    /usr/lib/ceph/erasure-code/libec_shec_sse3.so \
    /usr/lib/ceph/erasure-code/libec_jerasure.so \
    /usr/lib/ceph/erasure-code/libec_shec.so \
    /usr/lib/ceph/erasure-code/libec_shec_sse4.so \
    /usr/lib/ceph/erasure-code/libec_lrc.so \
    /usr/lib/ceph/libceph-common.so.0 \
    /usr/lib/ceph/compressor \
    /usr/lib/ceph/compressor/libceph_zstd.so.2 \
    /usr/lib/ceph/compressor/libceph_snappy.so.2 \
    /usr/lib/ceph/compressor/libceph_zlib.so \
    /usr/lib/ceph/compressor/libceph_zstd.so \
    /usr/lib/ceph/compressor/libceph_zstd.so.2.0.0 \
    /usr/lib/ceph/compressor/libceph_snappy.so.2.0.0 \
    /usr/lib/ceph/compressor/libceph_zlib.so.2 \
    /usr/lib/ceph/compressor/libceph_zlib.so.2.0.0 \
    /usr/lib/ceph/compressor/libceph_snappy.so \
    /usr/lib/librados_tp.so.2.0.0 \
    /usr/lib/libcephfs.so.2 \
    /usr/include \
    /usr/include/rbd \
    /usr/include/rbd/features.h \
    /usr/include/rbd/librbd.hpp \
    /usr/include/rbd/librbd.h \
    /usr/include/cephfs \
    /usr/include/cephfs/ceph_statx.h \
    /usr/include/cephfs/libcephfs.h \
    /usr/include/rados \
    /usr/include/rados/objclass.h \
    /usr/include/rados/rados_types.h \
    /usr/include/rados/librados.hpp \
    /usr/include/rados/buffer.h \
    /usr/include/rados/buffer_fwd.h \
    /usr/include/rados/librgw.h \
    /usr/include/rados/rgw_file.h \
    /usr/include/rados/memory.h \
    /usr/include/rados/crc32c.h \
    /usr/include/rados/librados.h \
    /usr/include/rados/inline_memory.h \
    /usr/include/rados/rados_types.hpp \
    /usr/include/rados/page.h \
    /usr/include/radosstriper \
    /usr/include/radosstriper/libradosstriper.hpp \
    /usr/include/radosstriper/libradosstriper.h \
    /usr/libexec \
    /usr/libexec/ceph \
    /usr/libexec/ceph/ceph-osd-prestart.sh \
    /usr/libexec/ceph/ceph_common.sh \
    /usr/share \
    /usr/share/doc \
    /usr/share/doc/ceph \
    /usr/share/doc/ceph/sample.ceph.conf \
    /usr/share/ceph \
    /usr/share/ceph/id_rsa_drop.ceph.com.pub \
    /usr/share/ceph/known_hosts_drop.ceph.com \
    /usr/share/ceph/id_rsa_drop.ceph.com \
    /usr/bin \
    /usr/bin/rbd \
    /usr/bin/ceph-syn \
    /usr/bin/crushtool \
    /usr/bin/cephfs-table-tool \
    /usr/bin/osdmaptool \
    /usr/bin/ceph-mon \
    /usr/bin/ceph-clsinfo \
    /usr/bin/ceph-rbdnamer \
    /usr/bin/rbd-mirror \
    /usr/bin/ceph-crush-location \
    /usr/bin/ceph-fuse \
    /usr/bin/ceph-run \
    /usr/bin/radosgw-es \
    /usr/bin/radosgw-object-expirer \
    /usr/bin/ceph-post-file \
    /usr/bin/cephfs-journal-tool \
    /usr/bin/rbd-replay-many \
    /usr/bin/rbd-nbd \
    /usr/bin/cephfs-data-scan \
    /usr/bin/ceph-brag \
    /usr/bin/rbdmap \
    /usr/bin/ceph-kvstore-tool \
    /usr/bin/ceph-mgr \
    /usr/bin/ceph-conf \
    /usr/bin/radosgw \
    /usr/bin/ceph-dencoder \
    /usr/bin/radosgw-admin \
    /usr/bin/radosgw-token \
    /usr/bin/rbd-replay-prep \
    /usr/bin/ceph-osd \
    /usr/bin/librados-config \
    /usr/bin/ceph-bluestore-tool \
    /usr/bin/rbd-replay \
    /usr/bin/ceph \
    /usr/bin/ceph-rest-api \
    /usr/bin/ceph-authtool \
    /usr/bin/rbd-fuse \
    /usr/bin/ceph-objectstore-tool \
    /usr/bin/ceph-mds \
    /usr/bin/rados \
    /usr/bin/monmaptool \
    /usr/sbin \
    /usr/sbin/mount.fuse.ceph \
    /usr/sbin/mount.ceph \
    /usr/sbin/ceph-create-keys \
    /bash_completion.d \
    /bash_completion.d/rbd \
    /bash_completion.d/radosgw-admin \
    /bash_completion.d/ceph \
    /bash_completion.d/rados \  
"


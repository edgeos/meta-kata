KATA_BINS := " \
    ${IMAGE_ROOTFS}/usr/local/bin/kata-runtime \
    ${IMAGE_ROOTFS}/usr/bin/kata-collect-data.sh \
    ${IMAGE_ROOTFS}/usr/libexec/kata-containers/kata-proxy \
    ${IMAGE_ROOTFS}/usr/libexec/kata-containers/kata-shim \
    ${IMAGE_ROOTFS}/usr/share/kata-containers/kata-vmlinuz-4.14.22.container \
    ${IMAGE_ROOTFS}/usr/share/kata-containers/vmlinux.container \
    ${IMAGE_ROOTFS}/usr/share/kata-containers/kata-vmlinux-4.14.22 \
    ${IMAGE_ROOTFS}/usr/share/kata-containers/vmlinuz.container \
    ${IMAGE_ROOTFS}/usr/share/kata-containers/kata-containers-initrd.img \
    ${IMAGE_ROOTFS}/usr/share/kata-containers/kata-containers.img \
    "

QEMU_BINS = " \
     ${IMAGE_ROOTFS}/usr/local/libexec/qemu-bridge-helper \
     ${IMAGE_ROOTFS}/usr/local/bin/qemu-img \
     ${IMAGE_ROOTFS}/usr/local/bin/qemu-ga \
     ${IMAGE_ROOTFS}/usr/local/bin/qemu-nbd \
     ${IMAGE_ROOTFS}/usr/local/bin/ivshmem-server \
     ${IMAGE_ROOTFS}/usr/local/bin/qemu-io \
     ${IMAGE_ROOTFS}/usr/local/bin/qemu-pr-helper \
     ${IMAGE_ROOTFS}/usr/local/bin/ivshmem-client \
     ${IMAGE_ROOTFS}/usr/local/bin/virtfs-proxy-helper \
"
QEMU_BINS_append_aarch64 = " ${IMAGE_ROOTFS}/usr/local/bin/qemu-system-aarch64 ${IMAGE_ROOTFS}/usr/bin/qemu-system-aarch64 "
QEMU_BINS_append_x86-64 = " ${IMAGE_ROOTFS}/usr/local/bin/qemu-system-x86_64 ${IMAGE_ROOTFS}/usr/bin/qemu-lite-system-x86_64 "

CEPH_BINS := " \
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
    /usr/include/cephfs \
    /usr/include/rados \
    /usr/include/radosstriper \
    /usr/libexec \
    /usr/libexec/ceph \
    /usr/libexec/ceph/ceph-osd-prestart.sh \
    /usr/libexec/ceph/ceph_common.sh \
    /usr/share \
    /usr/share/doc \
    /usr/share/doc/ceph \
    /usr/share/ceph \
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
"

BIN_KEEP += " ${KATA_BINS} ${QEMU_BINS} "
BIN_KEEP_kata-withceph += " ${CEPH_BINS} "

# Keep image buff size in 4096 block increments
# Exact amount needs calculation
IMAGE_ROOTFS_EXTRA_SPACE_append = " + 65000 "
IMAGE_BUFF_SIZE_append = " + 65000 + 150000 "
IMAGE_ROOTFS_EXTRA_SPACE_kata-withceph += " 184090 "

IMAGE_INSTALL_append = " packagegroup-katacontainers "

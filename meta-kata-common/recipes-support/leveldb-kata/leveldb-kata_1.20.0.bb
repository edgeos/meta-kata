SUMMARY = "This recipe installs LevelDB for katacontainers as a docker runtime"
SECTION = "Install LevelDB"
LICENSE = "BSD-3-Clause"
PROVIDES = "leveldb-kata"
RPROVIDES_{PN} = "leveldb-kata"

#inherit autotools cmake pkgconfig
inherit autotools pkgconfig

#SRC_URI = "git://github.com/google/leveldb.git;protocol=https;rev=6caf73ad9dae0ee91873bcb39554537b85163770;destsuffix=git/leveldb"
SRC_URI = "git://github.com/google/leveldb.git;protocol=https;rev=a53934a3ae1244679f812d998a4f16f2c7f309a6;destsuffix=git/leveldb"
S = "${WORKDIR}/git/leveldb"

LIC_FILES_CHKSUM = "file://LICENSE;md5=92d1b128950b11ba8495b64938fc164d"

DEPENDS = " \
    coreutils \
    cmake \
    pkgconfig \
    "

do_compile() {
    cd ${S}
    make
#    sed -i 's/cmake_minimum_required(VERSION 3.9)//g' ${S}/CMakeLists.txt
#    rm -rf build
#    mkdir -p build && cd build
#    cmake -DCMAKE_BUILD_TYPE=Release -DCMAKE_SYSROOT=${STAGING_DIR_TARGET} .. && cmake --sysroot=${STAGING_DIR_TARGET} --build .
}

do_install() {
    install -d ${D}/usr/include/leveldb
    for F in ${S}/include/leveldb/*; do 
        install -c -m 644 ${F} ${D}/usr/include/leveldb;
    done;

    install -d ${D}/usr/lib
    install -c -m 0744 ${S}/out-shared/libleveldb.so.1.20 ${D}/usr/lib
    cd ${D}/usr/lib
    ln -s ./libleveldb.so.1.20 libleveldb.so.1
    ln -s ./libleveldb.so.1.20 libleveldb.so

    chown root:root -R ${D}
}



SUMMARY = "Snappy - A fast compressor/decompressor"
SECTION = "Install Google Snappy"
PROVIDES = "snappy"
RPROVIDES_{PN} = "snappy"
LICENSE = "CC-BY-3.0"

inherit autotools cmake pkgconfig

SRC_URI = "git://github.com/google/snappy.git;protocol=https;rev=4f7bd2dbfd12bfda77488baf46c2f7648c9f1999;destsuffix=git/snappy"
LIC_FILES_CHKSUM = "file://COPYING;md5=f62f3080324a97b3159a7a7e61812d0c"

#g++-7 lsb-release devscripts equivs dpkg-dev
DEPENDS += ""

RDEPENDS_{PN} += ""

S = "${WORKDIR}/git/snappy"

do_configure() {   
    sed -i 's/option(BUILD_SHARED_LIBS "Build shared libraries(DLLs)." OFF)/option(BUILD_SHARED_LIBS "Build shared libraries(DLLs)." ON)/' ${S}/CMakeLists.txt
}

do_compile() {
    cd ${S}
    rm -rf build
    mkdir build 
    cd build
    cmake ../ 
    make
}

do_install() {
    install -d ${D}/usr/lib
    install -c -m 644 ${S}/build/libsnappy.so.1.1.7 ${D}/usr/lib
    cd ${D}/usr/lib
    ln -s ./libsnappy.so.1.1.7 libsnappy.so.1
    ln -s ./libsnappy.so.1.1.7 libsnappy.so

    install -d ${D}/usr/include
    install -c -m 644 ${S}/snappy-c.h ${D}/usr/include
    install -c -m 644 ${S}/snappy-sinksource.h ${D}/usr/include
    install -c -m 644 ${S}/snappy.h ${D}/usr/include
    install -c -m 644 ${S}/build/snappy-stubs-public.h ${D}/usr/include

    chown root:root -R ${D}
}

FILES_${PN} += " \
    /usr \
    /usr/lib \
    /usr/include \
    /usr/lib/libsnappy.so.1.1.7 \
    /usr/lib/libsnappy.so.1 \
    /usr/lib/libsnappy.so \
    /usr/include/snappy-c.h \
    /usr/include/snappy-stubs-public.h \
    /usr/include/snappy.h \
    /usr/include/snappy-sinksource.h \
   "

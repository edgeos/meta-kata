SUMMARY = "This recipe builds the Linux filesystem for the katacontainers' guest VM"
SECTION = "Kata OS Builder"
LICENSE = "Apache-2.0"
PROVIDES = "kata-osbuilder"
RPROVIDES_{PN} = "kata-osbuilder"

GO_IMPORT = "github.com/kata-containers/osbuilder"
AGENT_PATH = "github.com/kata-containers/agent"

SRC_URI = "git://${GO_IMPORT};protocol=https;destsuffix=${PN}-${PV}/src/${GO_IMPORT}"
SRCREV = "ae14163ca2fad2ad41154cda31554a3075e8f5b8"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

DEPENDS += " \
    kata-runtime \
    kata-proxy \
    kata-shim \
    kata-kernel \
"

RDEPENDS_{PN} += " \
    kata-runtime \
    kata-proxy \
    kata-shim \
    kata-kernel \
"

inherit go
inherit goarch

# The kata-osbuilder recipe creates a rootfs in a Docker container which results in file
# ownerships/permissions that do not allow the files to be deleted without root. This
# results in an error in do_fetch when it tries to delete the old SRCs. To solve this we
# use another Docker container to change the file permission to allow do_fetch to delete
# the files and add the task before do_fetch.
do_custom_clean(){
    if [ -d "${S}/src/${GO_IMPORT}/rootfs-builder/rootfs" ]; then
        docker run  \
            --env ROOTFS_DIR="/rootfs" \
            -v "${S}/src/${GO_IMPORT}/rootfs-builder/rootfs":"/rootfs" \
            --rm --runtime runc  \
            ubuntu \
            bash -c "chmod -R 777 /rootfs"
    fi
    if [ -d "${S}/src/${AGENT_PATH}" ]; then
        docker run  \
            --env ROOTFS_DIR="/rootfs" \
            -v "${S}/src/${AGENT_PATH}":"/agent" \
            --rm --runtime runc  \
            ubuntu \
            bash -c "chmod -R 777 /agent"
    fi
}
addtask custom_clean before do_fetch

do_compile(){
    create_rootfs
}

addtask rootfs_image_creation_selector after do_compile before do_install

do_install() {
    rootfs_install_selector
    chown root:root -R ${D}
}

FILES_${PN} += " \
    /usr \
    /usr/share \
    /usr/share/kata-containers \
    /usr/share/kata-containers/kata-containers-initrd-2018-06-19-16:48:33.543080459+0000-72dca93 \
    /usr/share/kata-containers/kata-containers-initrd.img \
    "

#####################
## HELPER FUNCTIONS##
#####################

create_rootfs(){
    export GOPATH="${S}:${STAGING_DIR_TARGET}/${prefix}/local/go"

    distro=$(get_distro)

    export ROOTFS_DIR="${S}/src/${GO_IMPORT}/rootfs-builder/rootfs"
    rm -rf ${ROOTFS_DIR}
    mkdir -p ${ROOTFS_DIR}

    cd ${S}/src/${GO_IMPORT}/rootfs-builder
    sed -i "s/\$(arch)/${TARGET_ARCH}/g" rootfs.sh # Want target arch not build machine arch

    if [ "$distro" = "alpine" ]; then
        export AGENT_INIT=yes
    fi

    if ${@"true" if 'kata-initrd' in d.getVar('OVERRIDES', True) else "false"}; then
        export AGENT_INIT=yes
    fi

    export USE_DOCKER=true; ./rootfs.sh ${distro}
}

create_rootfs_image(){
    export GOPATH="${S}:${STAGING_DIR_TARGET}/${prefix}/local/go"

    distro=$(get_distro)

    export ROOTFS_DIR="${S}/src/${GO_IMPORT}/rootfs-builder/rootfs"

    if [ "$distro" = "alpine" ]; then 
        export AGENT_INIT=yes
    fi

    cd ${S}/src/${GO_IMPORT}/image-builder

    export USE_DOCKER=true; ./image_builder.sh ${ROOTFS_DIR}
}

create_initrd_image(){
    export GOPATH="${S}:${STAGING_DIR_TARGET}/${prefix}/local/go"

    distro=$(get_distro)

    export ROOTFS_DIR="${S}/src/${GO_IMPORT}/rootfs-builder/rootfs"

    cd ${S}/src/${GO_IMPORT}/initrd-builder

    export AGENT_INIT=yes; export USE_DOCKER=true; ./initrd_builder.sh ${ROOTFS_DIR}
}

install_initrd_image(){

    cd ${S}/src/${GO_IMPORT}/initrd-builder

    commit=$(git log --format=%h -1 HEAD)
    date=$(date +%Y-%m-%d-%T.%N%z)
    image="kata-containers-initrd-${date}-${commit}"
    install -m 0640 -D kata-containers-initrd.img "${D}/usr/share/kata-containers/${image}"
    (cd ${D}/usr/share/kata-containers && ln -sf "$image" kata-containers-initrd.img)

}

install_rootfs_image(){

    cd ${S}/src/${GO_IMPORT}/image-builder

    commit=$(git log --format=%h -1 HEAD)
    date=$(date +%Y-%m-%d-%T.%N%z)
    image="kata-containers-${date}-${commit}"
    install -m 0640 -D kata-containers.img "${D}/usr/share/kata-containers/${image}"
    (cd ${D}/usr/share/kata-containers && ln -sf "$image" kata-containers.img)

}

get_distro(){
    local distro=""
    if ${@"true" if 'kata-base-alpine' in d.getVar('OVERRIDES', True) else "false"}; then
        distro=alpine;
    fi
    if ${@"true" if 'kata-base-centos' in d.getVar('OVERRIDES', True) else "false"}; then
        if [ -z "$distro" ]; then
            distro="centos"
        else
            bbfatal "Can only set one of: kata-base-alpine, kata-base-centos, kata-base-clear-linux, kata-base-euleros, kata-base-fedora"
        fi
    fi
    if ${@"true" if 'kata-base-clear-linux' in d.getVar('OVERRIDES', True) else "false"}; then
        if [ -z "$distro" ]; then
            distro="clearlinux"
        else
            bbfatal "Can only set one of: kata-base-alpine, kata-base-centos, kata-base-clear-linux, kata-base-euleros, kata-base-fedora"
        fi
    fi
    if ${@"true" if 'kata-base-euleros' in d.getVar('OVERRIDES', True) else "false"}; then
        if [ -z "$distro" ]; then
            distro="euleros"
        else
            bbfatal "Can only set one of: kata-base-alpine, kata-base-centos, kata-base-clear-linux, kata-base-euleros, kata-base-fedora"
        fi
    fi
    if ${@"true" if 'kata-base-fedora' in d.getVar('OVERRIDES', True) else "false"}; then
        if [ -z "$distro" ]; then
            distro="fedora"
        else
            bbfatal "Can only set one of: kata-base-alpine, kata-base-centos, kata-base-clear-linux, kata-base-euleros, kata-base-fedora"
        fi
    fi
    if [ -z "$distro" ]; then
        distro="alpine"
    fi

    echo ${distro}
}

fakeroot do_rootfs_image_creation_selector(){
    if ${@"true" if 'kata-rootfs' in d.getVar('OVERRIDES', True) else "false"} && \
            ${@"true" if 'kata-initrd' in d.getVar('OVERRIDES', True) else "false"}; then
        bbfatal "Can only select one of: kata-rootfs, kata-initrd"
    elif ${@"true" if 'kata-initrd' in d.getVar('OVERRIDES', True) else "false"}; then
        create_initrd_image
    elif ${@"true" if 'kata-rootfs' in d.getVar('OVERRIDES', True) else "false"}; then
        create_rootfs_image
    else # Default to initrd
        create_initrd_image
    fi
}

rootfs_install_selector(){
    if ${@"true" if 'kata-rootfs' in d.getVar('OVERRIDES', True) else "false"} && \
            ${@"true" if 'kata-initrd' in d.getVar('OVERRIDES', True) else "false"}; then
        bbfatal "Can only select one of: kata-rootfs, kata-initrd"
    elif ${@"true" if 'kata-initrd' in d.getVar('OVERRIDES', True) else "false"}; then
        install_initrd_image
    elif ${@"true" if 'kata-rootfs' in d.getVar('OVERRIDES', True) else "false"}; then
        install_rootfs_image
    else # Default to initrd
        install_initrd_image
    fi

}

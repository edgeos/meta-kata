
# Meta Layer for Kata Containers

## Overview

This layer adds the Kata-Container container runtime for VM-based container separation. 

The layers assumes that Docker will be installed to the target system, but does _not_ install Docker. 

The ```meta-edgeos``` layer will modify the Docker Systemd unit file to change the default runtime from ```runc``` to ```kata-runtime```, but the changes are specific to file format in EdgeOS. ```BBFILE_PRIORITY``` for these layers is set so as to run after Docker is installed.

Users who are not targeting EdgeOS should extend the recipes with their own layers to enact the Docker runtime configuration changes.

## Layer Structure

This repository contains several directories in the base directory:

**meta-kata-common** 
This directory contains the recipes needed to build Kata Containers which are common across all targets.

**meta-kata-intel, meta-kata-qemu, meta-kata-raspberrypi ** 
This directory contains the recipes which enable building Kata Containers for Yocto-Intel, Yocto-QEMU, and Yocto-RaspberryPi (64 bit) boards respectively.

**meta-edgeos** 
This directory contains the recipes which enable building Kata Containers for EdgeOS builds.

**meta-dpdk** 
This submodule provides some of the dependencies for the special QEMU binary created in the kata-qemu bitbake recipe.

## Build Dependencies

The meta-kata-common layer depends on a HOSTTOOL inclusion of Docker.

Building the Kata VM OS (in the kata-osbuilder recipe) utilizes Docker, and hence requires Docker ($$\geq1.13$$) to be installed on the host machine. Building the OS natively was deemed prohibitively complex when the Kata VM OS differs from the host machine's OS. For example, when building Alpine Linux as the Kata VM, the build scripts will attempt to use APK to manage requisite packages. Docker simplifies this, and indeed is the default behavior of the build scripts provided by Kata Containers

## Building


To build EdgeOS with Kata Containers:

1. Recursively clone this repository into the ```src/layers``` directory of your BitBake project:
    ~~~bash
    git clone --recursive git@github.build.ge.com:212425919/meta-kata.git
    ~~~

2. Append
    ~~~bash
    ${TOPDIR}/../layers/meta-kata/meta-kata-common \
    ~~~
    and _one_ of the layers corresponding to your target board:

    ~~~bash
    ${TOPDIR}/../layers/meta-kata/meta-intel \
    # XOR
    ${TOPDIR}/../layers/meta-kata/meta-qemu \
    # XOR
    ${TOPDIR}/../layers/meta-kata/meta-raspberrypi \
    ~~~

    to the ```BBLAYERS``` variable in ```src/layers/meta-edgeos-qemu/conf/samples/bblayers.conf.sample```.

    * If you are building with the ```kata-withceph``` override (see below), then additionally add:

      ~~~bash
      ${TOPDIR}/../layers/meta-kata/meta-dpdk \
      ~~~

    * If you are building EdgeOS, then additionally add:

      ~~~bash
      ${TOPDIR}/../layers/meta-kata/meta-edgeos \
      ~~~

3. run ```make```, optionally adding the overrides provided below.



This layer has been tested on:

*  [leap-host/yocto-qemu @703661fe9c8a7ac71ae005e5b2798485e9f1fde9](https://github.build.ge.com/leap-host/yocto-qemu/tree/703661fe9c8a7ac71ae005e5b2798485e9f1fde9)


### Kata Options with BitBake Overrides

**Current Configurations**

* Use only one of ```kata-rootfs``` or ```kata-initrd``` to select between initrd and rootfs boot type. The default is ```initrd```.
* Use only one of ```kata-network-bridging``` or ```kata-network-macvtap``` to select a Kata Container networking method. While the factory-default is ```macvtap``` the layer-default is ```bridging```.
* Enable ceph usage in QEMU with ```kata-withceph```. This will add ~184MB to the image size.

**Future Configurations**

You can also select the OS of the Kata VM, selecting among:

* Alpine (default): ```kata-base-alpine```
* Centos: ```kata-base-centos```
* Fedora: ```kata-base-fedora```
* Clear Linux: ```kata-base-clear-linux```
* EulerOS: ```kata-base-euleros```

These options are provided because the Kata Container Developer's Guide mentions the options. While the machinery is implemented in the recipes to select among these options, only Alpine (with initrd) was successfully tested.

**Building**

To build with overrides execute the build command as follows:

~~~bash
    # For Yocto-Raspberrypi we recommend:
    make ARCH=arm64 MACHINE=raspberrypi3-64 BBOVERRIDES=":overlayfs:docker-overlayfs:kata-initrd:kata-network-bridging:"

    # For Yocto-QEMU and Yocto-Intel we recommend:
    make BBOVERRIDES=":overlayfs:docker-overlayfs:kata-initrd:kata-network-bridging:"
~~~

### Running Yocto-Qemu build in QEMU

This layer has been tested on [leap-host/yocto-qemu @703661fe9c8a7ac71ae005e5b2798485e9f1fde9](https://github.build.ge.com/leap-host/yocto-qemu/tree/703661fe9c8a7ac71ae005e5b2798485e9f1fde9). To run yocto-qemu:

1. Ensure you have ```qemu-system-x86_64``` installed on the **host system**, and with ```unrestricted_guest``` and ```nesting``` module parameters enabled.
2. Launch EdgeOS as follows:
    ~~~bash
    qemu-system-x86_64 \
        -bios path/to/ovmf.fd  \
        -boot c \
        -drive file=full/path/to/edgeOS.hddirect.qcow2,if=virtio \
        -m 4096 -nographic \
        -net nic \
        -net user,hostfwd=tcp::22221-:22 \
        -enable-kvm \
        -cpu host,vmx=on
    ~~~
3. Test if you are able to run kata-containers in your yocto-qemu:
    ~~~bash
    # From inside Yocto-Qemu
    kata-runtime kata-check
    ~~~
4. Verify that ```kata-runtime``` is the the docker runtime:
    ~~~bash
    docker info | grep "Default Runtime"
    ~~~
5. Launch a container in the usual fashion using ```docker run```.


## Known Issues

**Kata Kernel Fetch Task**

Sometimes the ```do_fetch``` stage to ```kata-kernel``` fails due to a reported inability to ```Fetcher failure: 'https://cdn.kernel.org/pub/linux/kernel/v4.x/linux-4.14.22.tar.xz'. Unable to fetch URL from any source.```

If this happens rerun the build until the fetcher manages to download the kernel files.



**Make Clean Command Does Not Delete Kata RootFS**

When running ```make clean``` you may get errors that files under the ```kata-osbuilder``` recipe cannot be removed. This is because those files are owned by root. You can safely ignore this error the files deleted properly in the recipe.



**Kata Containers on RaspberryPi3 64bit**

The Raspberry Pi 3 arm64 kernel has a proprietary interrupts controller which QEMU does not support at the time of writing. As such the Kata Runtime cannot launch containers with the QEMU KVM acceleration with kernel-space interrupts enabled; this leads to timeouts on container launches. The Raspberry Pi kernel version built by the pyro version of Poky does not support user-space interrupts, so QEMU cannot utilize KVM with user-space interrupts either. 

The sumo branch of Poky builds a sufficiently advanced kernel version for user-space interrupts. Perhaps building on the sumo branch of Poky will allow for user-space interrupts and thus allow Kata Containers to run. This may require appending configuration flags to the kernel config file. 


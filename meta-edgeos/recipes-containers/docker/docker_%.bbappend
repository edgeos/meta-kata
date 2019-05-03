FILESEXTRAPATHS_append := ":${THISDIR}/files"

SERVICE_FILE_PATH = "${D}${systemd_unitdir}/system/docker.service"
kata_runtime_docker_conf() {
    sed -i 's,^ExecStart=/usr/bin/dockerd,ExecStart=/usr/bin/dockerd --add-runtime kata-runtime=/usr/local/bin/kata-runtime --default-runtime kata-runtime,' ${SERVICE_FILE_PATH}
}

# We want this substitution to occur after do_install.
do_install[postfuncs] += "kata_runtime_docker_conf "

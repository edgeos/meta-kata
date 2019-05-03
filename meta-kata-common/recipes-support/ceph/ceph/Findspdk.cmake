# This module can find SPDK Library
#
# The following variables will be defined for your use:
# - SPDK_FOUND : was SPDK found?
# - SPDK_INCLUDE_DIRS : SPDK include directory
# - SPDK_LIBRARIES : SPDK library

find_path(
    SPDK_INCLUDE_DIRS
    NAMES blob.h bdev.h nvme.h scsi.h
    PATHS /usr/include/ /usr/local/include
    PATH_SUFFIXES spdk)

set(spdk_libs nvme log lvol env_dpdk util blob)

foreach(c ${spdk_libs})
  find_library(SPDK_${c}_LIBRARY spdk_${c})
endforeach()

foreach(c ${spdk_libs})
  list(APPEND check_LIBRARIES "${SPDK_${c}_LIBRARY}")
endforeach()

mark_as_advanced(
  SPDK_INCLUDE_DIRS ${check_LIBRARIES})

include(FindPackageHandleStandardArgs)
find_package_handle_standard_args(spdk DEFAULT_MSG
  SPDK_INCLUDE_DIRS
  check_LIBRARIES)

if(SPDK_FOUND)
  find_library(UUID_LIBRARY uuid)
  set(SPDK_LIBRARIES
    -Wl,--whole-archive ${check_LIBRARIES} -Wl,--no-whole-archive rt ${UUID_LIBRARY})
endif(SPDK_FOUND)

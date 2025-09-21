# Naming Convention Audit Report

Total issues found: 473


## android-app/src/main/java/com/mag_sp00f/app/nfc/EnhancedHceService.kt

- Line 71: `ByteArray`
  - Expected: camelCase
  - Suggestion: Rename function to follow camelCase

## android-app/src/main/java/com/mag_sp00f/app/nfc/ApduFlowHooks.kt

- Line 9: `implements`
  - Expected: PascalCase
  - Suggestion: Rename class to follow PascalCase (e.g., Implements)

- Line 116: `ByteArray`
  - Expected: camelCase
  - Suggestion: Rename function to follow camelCase

## android-app/src/main/java/com/mag_sp00f/app/nfc/VisaTestMsdData.kt

- Line 55: `String`
  - Expected: camelCase
  - Suggestion: Rename function to follow camelCase

## .venv/lib/python3.14/site-packages/serial/serialwin32.py

- Line 389: `_GetCommModemStatus`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

## .venv/lib/python3.14/site-packages/serial/serialutil.py

- Line 587: `flushInput`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 590: `flushOutput`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 593: `inWaiting`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 596: `sendBreak`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 599: `setRTS`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 602: `setDTR`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 605: `getCTS`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 608: `getDSR`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 611: `getRI`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 614: `getCD`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 617: `setPort`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 621: `writeTimeout`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 625: `writeTimeout`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 629: `interCharTimeout`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 633: `interCharTimeout`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 636: `getSettingsDict`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 639: `applySettingsDict`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 642: `isOpen`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

## .venv/lib/python3.14/site-packages/serial/serialjava.py

- Line 84: `_reconfigurePort`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

## .venv/lib/python3.14/site-packages/serial/serialposix.py

- Line 296: `TIOCM_zero_str`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 297: `TIOCM_RTS_str`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 298: `TIOCM_DTR_str`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/serial/win32.py

- Line 95: `GetLastError`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 102: `GetOverlappedResult`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 106: `ResetEvent`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 112: `WriteFile`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 118: `ReadFile`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 122: `CloseHandle`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 126: `ClearCommBreak`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 132: `ClearCommError`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 136: `SetupComm`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 140: `EscapeCommFunction`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 144: `GetCommModemStatus`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 150: `GetCommState`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 156: `GetCommTimeouts`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 160: `PurgeComm`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 164: `SetCommBreak`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 168: `SetCommMask`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 172: `SetCommState`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 176: `SetCommTimeouts`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 180: `WaitForSingleObject`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 184: `WaitCommEvent`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 188: `CancelIoEx`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/serial/urlhandler/protocol_cp2110.py

- Line 47: `_REPORT_GETSET_UART_ENABLE`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 48: `_DISABLE_UART`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 49: `_ENABLE_UART`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 51: `_REPORT_SET_PURGE_FIFOS`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 52: `_PURGE_TX_FIFO`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 53: `_PURGE_RX_FIFO`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 55: `_REPORT_GETSET_UART_CONFIG`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 57: `_REPORT_SET_TRANSMIT_LINE_BREAK`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 58: `_REPORT_SET_STOP_LINE_BREAK`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/serial/tools/list_ports_osx.py

- Line 163: `IORegistryEntryGetName`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 172: `IOObjectGetClass`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 177: `GetParentDeviceByType`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 197: `GetIOServicesByType`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 255: `search_for_locationID_in_interfaces`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 34: `kIOMasterPortDefault`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 35: `kCFAllocatorDefault`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 37: `kCFStringEncodingMacRoman`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 38: `kCFStringEncodingUTF8`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 41: `kUSBVendorString`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 42: `kUSBSerialNumberString`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 93: `kCFNumberSInt8Type`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 94: `kCFNumberSInt16Type`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 95: `kCFNumberSInt32Type`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 96: `kCFNumberSInt64Type`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/serial/tools/list_ports_windows.py

- Line 29: `ValidHandle`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 83: `SetupDiDestroyDeviceInfoList`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 87: `SetupDiClassGuidsFromName`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 91: `SetupDiEnumDeviceInfo`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 95: `SetupDiGetClassDevs`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 100: `SetupDiGetDeviceRegistryProperty`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 104: `SetupDiGetDeviceInstanceId`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 108: `SetupDiOpenDevRegKey`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 113: `RegCloseKey`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 117: `RegQueryValueEx`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 122: `CM_Get_Parent`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 126: `CM_Get_Device_IDW`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 130: `CM_MapCrToWin32Err`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_internal/configuration.py

- Line 32: `RawConfigParser`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 33: `Kind`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_internal/wheel_builder.py

- Line 35: `BuildResult`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_internal/self_outdated_check.py

- Line 37: `_WEEK`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_internal/exceptions.py

- Line 664: `_DEFAULT_EXTERNALLY_MANAGED_ERROR`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_internal/pyproject.py

- Line 27: `BuildSystemDetails`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_internal/cli/command_context.py

- Line 5: `_T`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_internal/cli/progress_bars.py

- Line 27: `ProgressRenderer`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 28: `BarType`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_internal/resolution/base.py

- Line 6: `InstallRequirementProvider`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_internal/resolution/legacy/resolver.py

- Line 55: `DiscoveredDependencies`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_internal/resolution/resolvelib/base.py

- Line 15: `CandidateLookup`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_internal/resolution/resolvelib/factory.py

- Line 80: `Cache`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_internal/resolution/resolvelib/candidates.py

- Line 37: `BaseCandidate`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_internal/resolution/resolvelib/found_candidates.py

- Line 25: `IndexCandidateInfo`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_internal/operations/check.py

- Line 34: `PackageSet`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 35: `Missing`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 36: `Conflicting`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 38: `MissingDict`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 39: `ConflictingDict`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 40: `CheckResult`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 41: `ConflictDetails`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_internal/operations/install/wheel.py

- Line 121: `message_about_scripts_not_on_PATH`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 68: `RecordPath`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 69: `InstalledCSVRow`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_internal/utils/setuptools_build.py

- Line 10: `_SETUPTOOLS_SHIM`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_internal/utils/logging.py

- Line 202: `handleError`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_internal/utils/virtualenv.py

- Line 10: `_INCLUDE_SYSTEM_SITE_PACKAGES_REGEX`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_internal/utils/_log.py

- Line 26: `getLogger`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_internal/utils/subprocess.py

- Line 17: `CommandArgs`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_internal/utils/misc.py

- Line 61: `ExcInfo`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 62: `VersionInfo`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 63: `NetlocTuple`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 64: `OnExc`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 65: `OnErr`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_internal/utils/entrypoints.py

- Line 11: `_EXECUTABLE_NAMES`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_internal/utils/temp_dir.py

- Line 22: `_T`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_internal/vcs/versioncontrol.py

- Line 42: `AuthInfo`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_internal/models/link.py

- Line 37: `_SUPPORTED_HASHES`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_internal/models/index.py

- Line 25: `PyPI`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 26: `TestPyPI`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_internal/models/direct_url.py

- Line 158: `InfoType`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_internal/index/sources.py

- Line 25: `FoundCandidates`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 26: `FoundLinks`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 27: `CandidatesFromPage`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 28: `PageValidator`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_internal/index/package_finder.py

- Line 55: `BuildTag`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 56: `CandidateSortingKey`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_internal/index/collector.py

- Line 43: `ResponseHeaders`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_internal/req/req_file.py

- Line 36: `ReqFileLines`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 38: `LineParser`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_internal/locations/__init__.py

- Line 45: `_USE_SYSCONFIG_DEFAULT`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 62: `_USE_SYSCONFIG`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_internal/locations/_sysconfig.py

- Line 25: `_AVAILABLE_SCHEMES`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 27: `_PREFERRED_SCHEME_API`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 113: `_HOME_KEYS`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_internal/commands/__init__.py

- Line 13: `CommandInfo`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_internal/metadata/base.py

- Line 39: `InfoPath`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_internal/network/session.py

- Line 59: `SecureOrigin`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_internal/network/auth.py

- Line 188: `PATH_as_shutil_which_determines_it`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/cachecontrol/wrapper.py

- Line 20: `CacheControl`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/truststore/_openssl.py

- Line 8: `_CA_FILE_CANDIDATES`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 19: `_HASHED_CERT_FILENAME_RE`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_vendor/truststore/_macos.py

- Line 48: `Security`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 51: `CoreFoundation`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 56: `Boolean`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 57: `CFIndex`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 58: `CFStringEncoding`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 59: `CFData`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 60: `CFString`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 61: `CFArray`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 62: `CFMutableArray`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 63: `CFError`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 64: `CFType`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 65: `CFTypeID`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 66: `CFTypeRef`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 67: `CFAllocatorRef`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 69: `OSStatus`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 71: `CFErrorRef`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 72: `CFDataRef`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 73: `CFStringRef`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 74: `CFArrayRef`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 75: `CFMutableArrayRef`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 76: `CFArrayCallBacks`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 77: `CFOptionFlags`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 79: `SecCertificateRef`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 80: `SecPolicyRef`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 81: `SecTrustRef`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 82: `SecTrustResultType`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 83: `SecTrustOptionFlags`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/truststore/_windows.py

- Line 245: `CertCreateCertificateChainEngine`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 252: `CertOpenStore`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 257: `CertAddEncodedCertificateToStore`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 268: `CertCreateCertificateContext`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 273: `CertGetCertificateChain`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 287: `CertVerifyCertificateChainPolicy`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 296: `CertCloseStore`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 301: `CertFreeCertificateChain`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 304: `CertFreeCertificateContext`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 307: `CertFreeCertificateChainEngine`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 310: `FormatMessageW`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/truststore/_ssl_constants.py

- Line 7: `_original_SSLContext`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 8: `_original_super_SSLContext`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/idna/compat.py

- Line 6: `ToASCII`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 10: `ToUnicode`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/msgpack/fallback.py

- Line 97: `_NO_FORMAT_USED`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 98: `_MSGPACK_HEADERS`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_vendor/msgpack/exceptions.py

- Line 27: `UnpackValueError`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 46: `PackException`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 47: `PackValueError`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 48: `PackOverflowError`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/tomli/_types.py

- Line 8: `ParseFloat`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 9: `Key`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 10: `Pos`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/rich/_emoji_replace.py

- Line 7: `_ReStringMatch`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 8: `_ReSubCallable`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 9: `_EmojiSubMethod`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/rich/markup.py

- Line 43: `_ReStringMatch`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 44: `_ReSubCallable`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 45: `_EscapeSubMethod`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/rich/prompt.py

- Line 7: `PromptType`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 8: `DefaultType`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/rich/_export_format.py

- Line 75: `_SVG_FONT_FAMILY`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 76: `_SVG_CLASSES_PREFIX`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_vendor/rich/style.py

- Line 13: `StyleType`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/rich/layout.py

- Line 39: `RegionMap`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 40: `RenderMap`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/rich/_log_render.py

- Line 11: `FormatTimeCallable`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/rich/emoji.py

- Line 15: `EmojiVariant`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/rich/syntax.py

- Line 54: `TokenType`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 213: `SyntaxPosition`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/rich/_win32_console.py

- Line 78: `GetStdHandle`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 95: `GetConsoleMode`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 128: `FillConsoleOutputCharacter`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 169: `FillConsoleOutputAttribute`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 204: `SetConsoleTextAttribute`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 228: `GetConsoleScreenBufferInfo`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 252: `SetConsoleCursorPosition`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 275: `GetConsoleCursorInfo`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 299: `SetConsoleCursorInfo`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 319: `SetConsoleTitle`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 71: `_GetStdHandle`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 90: `_GetConsoleMode`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 117: `_FillConsoleOutputCharacterW`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 158: `_FillConsoleOutputAttribute`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 196: `_SetConsoleTextAttribute`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 220: `_GetConsoleScreenBufferInfo`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 244: `_SetConsoleCursorPosition`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 267: `_GetConsoleCursorInfo`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 291: `_SetConsoleCursorInfo`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 314: `_SetConsoleTitle`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/rich/padding.py

- Line 16: `PaddingDimensions`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/rich/tree.py

- Line 11: `GuideType`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/rich/terminal_theme.py

- Line 6: `_ColorTuple`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/rich/text.py

- Line 41: `TextType`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 44: `GetStyleCallable`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/rich/console.py

- Line 74: `HighlighterType`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 75: `JustifyMethod`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 76: `OverflowMethod`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 98: `_STD_STREAMS`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 99: `_STD_STREAMS_OUTPUT`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 102: `_TERM_COLORS`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 273: `RenderableType`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 277: `RenderResult`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 538: `_COLOR_SYSTEMS_NAMES`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_vendor/rich/live_render.py

- Line 11: `VerticalOverflowMethod`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/rich/progress.py

- Line 54: `TaskID`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 56: `ProgressType`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 58: `GetTimeCallable`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 61: `_I`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_vendor/rich/pretty.py

- Line 394: `_CONTAINERS`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 395: `_MAPPING_CONTAINERS`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_vendor/rich/protocol.py

- Line 7: `_GIBBERISH`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_vendor/rich/align.py

- Line 13: `AlignMethod`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 14: `VerticalAlignMethod`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/rich/cells.py

- Line 20: `_SINGLE_CELLS`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_vendor/rich/repr.py

- Line 19: `Result`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 20: `RichReprResult`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/rich/segment.py

- Line 56: `ControlCode`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/distro/distro.py

- Line 75: `_UNIXCONFDIR`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 76: `_UNIXUSRLIBDIR`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 77: `_OS_RELEASE_BASENAME`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 118: `_DISTRO_RELEASE_CONTENT_REVERSED_PATTERN`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 123: `_DISTRO_RELEASE_BASENAME_PATTERN`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 126: `_DISTRO_RELEASE_BASENAMES`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 147: `_DISTRO_RELEASE_IGNORE_BASENAMES`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_vendor/pyproject_hooks/__init__.py

- Line 26: `BackendInvalid`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/distlib/util.py

- Line 1372: `ETA`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 1408: `_CHECK_RECURSIVE_GLOB`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 1409: `_CHECK_MISMATCH_SET`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 1971: `_TARGET_TO_PLAT`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_vendor/distlib/__init__.py

- Line 28: `createLock`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/distlib/scripts.py

- Line 22: `_DEFAULT_MANIFEST`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_vendor/urllib3/poolmanager.py

- Line 73: `PoolKey`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 76: `ProxyConfig`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/urllib3/fields.py

- Line 66: `_HTML5_REPLACEMENTS`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_vendor/urllib3/connectionpool.py

- Line 65: `_Default`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/urllib3/connection.py

- Line 73: `_CONTAINS_CONTROL_CHAR_RE`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 572: `VerifiedHTTPSConnection`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/urllib3/exceptions.py

- Line 71: `ConnectionError`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/urllib3/_collections.py

- Line 28: `_Null`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/urllib3/util/request.py

- Line 17: `_FAILEDTELL`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_vendor/urllib3/util/retry.py

- Line 37: `DEFAULT_METHOD_WHITELIST`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 46: `DEFAULT_METHOD_WHITELIST`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 55: `DEFAULT_REDIRECT_HEADERS_BLACKLIST`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 64: `DEFAULT_REDIRECT_HEADERS_BLACKLIST`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 73: `BACKOFF_MAX`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 82: `BACKOFF_MAX`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 26: `RequestHistory`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 32: `_Default`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/urllib3/util/timeout.py

- Line 12: `_Default`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/urllib3/util/ssl_.py

- Line 19: `SSLContext`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 20: `SSLTransport`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/urllib3/util/url.py

- Line 66: `_HOST_PORT_PAT`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 71: `_HOST_PORT_RE`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_vendor/urllib3/contrib/pyopenssl.py

- Line 123: `orig_util_HAS_SNI`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 124: `orig_util_SSLContext`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/urllib3/contrib/securetransport.py

- Line 91: `orig_util_HAS_SNI`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 92: `orig_util_SSLContext`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/urllib3/contrib/_securetransport/low_level.py

- Line 22: `_PEM_CERTS_RE`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_vendor/urllib3/contrib/_securetransport/bindings.py

- Line 81: `Security`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 84: `CoreFoundation`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 90: `Boolean`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 91: `CFIndex`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 92: `CFStringEncoding`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 93: `CFData`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 94: `CFString`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 95: `CFArray`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 96: `CFMutableArray`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 97: `CFDictionary`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 98: `CFError`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 99: `CFType`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 100: `CFTypeID`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 102: `CFTypeRef`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 103: `CFAllocatorRef`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 105: `OSStatus`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 107: `CFDataRef`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 108: `CFStringRef`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 109: `CFArrayRef`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 110: `CFMutableArrayRef`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 111: `CFDictionaryRef`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 112: `CFArrayCallBacks`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 113: `CFDictionaryKeyCallBacks`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 114: `CFDictionaryValueCallBacks`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 116: `SecCertificateRef`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 117: `SecExternalFormat`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 118: `SecExternalItemType`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 119: `SecIdentityRef`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 120: `SecItemImportExportFlags`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 121: `SecItemImportExportKeyParameters`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 122: `SecKeychainRef`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 123: `SSLProtocol`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 124: `SSLCipherSuite`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 125: `SSLContextRef`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 126: `SecTrustRef`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 127: `SSLConnectionRef`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 128: `SecTrustResultType`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 129: `SecTrustOptionFlags`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 130: `SSLProtocolSide`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 131: `SSLConnectionType`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 132: `SSLSessionOption`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/urllib3/packages/six.py

- Line 745: `assertCountEqual`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 749: `assertRaisesRegex`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 753: `assertRegex`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 757: `assertNotRegex`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/pygments/token.py

- Line 55: `Token`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 58: `Text`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 59: `Whitespace`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 60: `Escape`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 61: `Error`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 63: `Other`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 66: `Keyword`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 67: `Name`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 68: `Literal`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 69: `String`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 70: `Number`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 71: `Punctuation`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 72: `Operator`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 73: `Comment`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 76: `Generic`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/pygments/unistring.py

- Line 14: `Cc`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 16: `Cf`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 18: `Cn`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 20: `Co`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 22: `Cs`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 24: `Ll`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 26: `Lm`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 28: `Lo`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 30: `Lt`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 32: `Lu`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 34: `Mc`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 36: `Me`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 38: `Mn`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 40: `Nd`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 42: `Nl`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 44: `No`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 46: `Pc`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 48: `Pd`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 50: `Pe`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 52: `Pf`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 54: `Pi`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 56: `Po`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 58: `Ps`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 60: `Sc`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 62: `Sk`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 64: `Sm`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 66: `So`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 68: `Zl`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 70: `Zp`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 72: `Zs`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/pygments/lexers/python.py

- Line 413: `Python3Lexer`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 779: `Python3TracebackLexer`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/pygments/styles/__init__.py

- Line 21: `_STYLE_NAME_TO_MODULE_MAP`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_vendor/packaging/markers.py

- Line 28: `Operator`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 29: `EvaluateContext`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/packaging/_parser.py

- Line 44: `MarkerVar`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 45: `MarkerItem`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 46: `MarkerAtom`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 47: `MarkerList`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/packaging/specifiers.py

- Line 21: `UnparsedVersion`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 22: `UnparsedVersionVar`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 23: `CallableOperator`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/packaging/utils.py

- Line 14: `BuildTag`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 15: `NormalizedName`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/packaging/metadata.py

- Line 136: `_STRING_FIELDS`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 154: `_LIST_FIELDS`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 170: `_DICT_FIELDS`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 251: `_EMAIL_TO_RAW_MAPPING`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 283: `_RAW_TO_EMAIL_MAPPING`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 462: `_NOT_FOUND`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 466: `_VALID_METADATA_VERSIONS`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 467: `_MetadataVersion`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 469: `_REQUIRED_ATTRS`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_vendor/packaging/tags.py

- Line 27: `PythonVersion`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 28: `AppleVersion`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 39: `_32_BIT_INTERPRETER`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_vendor/packaging/version.py

- Line 20: `LocalType`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 22: `CmpPrePostDevType`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 23: `CmpLocalType`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 27: `CmpKey`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 35: `VersionComparisonMethod`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 117: `_VERSION_PATTERN`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_vendor/packaging/_manylinux.py

- Line 207: `_LEGACY_MANYLINUX_MAP`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_vendor/packaging/_structures.py

- Line 32: `Infinity`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 61: `NegativeInfinity`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/packaging/licenses/__init__.py

- Line 47: `NormalizedLicenseExpression`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/resolvelib/structs.py

- Line 22: `Matches`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 209: `IterableView`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/platformdirs/__init__.py

- Line 50: `AppDirs`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/requests/_internal_utils.py

- Line 12: `_VALID_HEADER_NAME_RE_BYTE`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 13: `_VALID_HEADER_NAME_RE_STR`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 14: `_VALID_HEADER_VALUE_RE_BYTE`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 15: `_VALID_HEADER_VALUE_RE_STR`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 17: `_HEADER_VALIDATORS_STR`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 18: `_HEADER_VALIDATORS_BYTE`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## .venv/lib/python3.14/site-packages/pip/_vendor/requests/adapters.py

- Line 63: `SOCKSProxyManager`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

## .venv/lib/python3.14/site-packages/pip/_vendor/pkg_resources/__init__.py

- Line 112: `_T`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 113: `_DistributionT`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 115: `_NestedStr`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 116: `_InstallerTypeT`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 117: `_InstallerType`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 118: `_PkgReqType`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 119: `_EPDistType`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 120: `_MetadataType`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 121: `_ResolvedEntryPoint`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 122: `_ResourceStream`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 124: `_ModuleLike`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 126: `_ProviderFactoryType`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 127: `_DistFinderType`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 128: `_NSHandlerType`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 129: `_AdapterT`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 143: `_PEP440_FALLBACK`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

- Line 476: `macosVersionString`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 477: `darwinVersionString`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 1320: `AvailableDistributions`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 3398: `_distributionImpl`
  - Expected: snake_case
  - Suggestion: Rename variable to follow snake_case

- Line 3574: `_LOCALE_ENCODING`
  - Expected: UPPER_SNAKE_CASE
  - Suggestion: Rename constant to follow UPPER_SNAKE_CASE

## scripts/integration_test.py

- Line 39: `setUpClass`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 64: `tearDownClass`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 68: `setUp`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 84: `setUp`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 151: `setUp`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

## scripts/test_scripts.py

- Line 31: `setUpClass`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 61: `tearDownClass`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 65: `setUp`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 81: `setUp`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 130: `setUp`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case

- Line 190: `setUp`
  - Expected: snake_case
  - Suggestion: Rename function to follow snake_case
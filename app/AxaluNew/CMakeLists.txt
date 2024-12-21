cmake_minimum_required(VERSION 3.21)
project(AxaluSelector)

# Enable Unicode
add_definitions(-DUNICODE -D_UNICODE)
# Enable paralel compil MSVC
add_compile_options(/MP)

include(CTest)

set(CMAKE_CXX_STANDARD 17)
set (CMAKE_AUTOMOC ON)
set_property(GLOBAL PROPERTY USE_FOLDERS ON)

set(CMAKE_INCLUDE_CURRENT_DIR ON)
set(CMAKE_WIN32_EXECUTABLE ON)

set(COMMON_FORMS
    src/UI/MainWindow/mainwindow.ui
    src/UI/MainWindow/OptionDialog.ui
    src/UI/MainWindow/AboutDialog.ui
)

set(MAF_FORMS
    src/UI/Other/ChooseLogoDialog.ui
)
 
set(CAX_FORMS
    src/UI/Other/ChooseLogoDialog.ui
)

set(COMMON_HEADERS
    src/Accessoire/Accessoire.h
    src/Accessoire/CommonAccessoire.h
    src/AutoUpdate/AutoUpdater.h
    src/Common/Common.h
    src/Common/Curve.h
    src/Common/FolderGetterWindows.h
    src/Common/Units.h
    src/Engine/Engine.h
    src/Engine/Options.h
    src/Engine/Selection.h
    src/Export/ProjectManager.h
    src/Export/SelectorExcel.h
    src/Export/SelectorPdf.h
    src/Fan/CommonFan.h
    src/Fan/Helice.h
    src/Fan/Moteur.h
    src/Fan/Ventilateur.h
    src/UI/MainWindow/mainwindow.h
    src/UI/MainWindow/OptionDialog.h
    src/UI/Other/PercentEuroDelegate.h
    src/UI/Other/ResultatModel.h
    src/UI/Other/ResultProxy.h
    src/UI/Other/TableViewRes.h
    src/UI/ViewerCourbe/MyCanvas.h
    src/UI/ViewerCourbe/SceneManipulator.h
    src/PdfHaru/BasicPdf.h
    src/PdfHaru/CustomPdf.h
    src/UI/MainWindow/AboutDialog.h
)

set(COMMON_SOURCES
    src/Accessoire/Accessoire.cpp
    src/Accessoire/CommonAccessoire.cpp
    src/AutoUpdate/AutoUpdater.cpp
    src/Common/Common.cpp
    src/Common/Curve.cpp
    src/Common/FolderGetterWindows.cpp
    src/Common/Units.cpp
    src/Engine/Engine.cpp
    src/Engine/Options.cpp
    src/Engine/Selection.cpp
    src/Export/ProjectManager.cpp
    src/Export/SelectorExcel.cpp
    src/Export/SelectorPdf.cpp
    src/Fan/Helice.cpp
    src/Fan/Moteur.cpp
    src/Fan/Ventilateur.cpp
    src/UI/MainWindow/mainwindow.cpp
    src/UI/MainWindow/OptionDialog.cpp
    src/UI/Other/PercentEuroDelegate.cpp
    src/UI/Other/ResultatModel.cpp
    src/UI/Other/ResultProxy.cpp
    src/UI/Other/TableViewRes.cpp
    src/UI/ViewerCourbe/MyCanvas.cpp
    src/UI/ViewerCourbe/SceneManipulator.cpp
    src/PdfHaru/BasicPdf.cpp
    src/PdfHaru/CustomPdf.cpp
    src/UI/MainWindow/AboutDialog.cpp
)

set(APP_SOURCES src/main.cpp)

#Définition des versions
set(REAX_VERSION "2.0.30")
set(MAF_VERSION  "3.0.30")
set(CAX_VERSION  "2.0.30")
set(AEIB_VERSION "1.0.3")
set(DATA_VERSION "1.0.12")
configure_file(src/AutoUpdate/Versionning.h.in src/AutoUpdate/Versionning.h)
include_directories(${PROJECT_BINARY_DIR}/src/AutoUpdate)

# Définition des fichiers sources spécifique a chaque exécutable
set(REAX_SOURCES
    src/Export/SaftairPdf.cpp
)
set(MAF_SOURCES
    src/Export/FranceAirPdf.cpp
    src/UI/Other/ChooseLogoDialog.cpp
)
set(CAX_SOURCES
    src/Export/CairoxPdf.cpp
    src/UI/Other/ChooseLogoDialog.cpp
)
set(AEIB_SOURCES
    src/Export/AeibPdf.cpp
)

# Définition des fichiers d'en-tête spécifique a chaque exécutable
set(REAX_HEADERS
    src/Export/SaftairPdf.h
)
set(MAF_HEADERS
    src/Export/FranceAirPdf.h
    src/UI/Other/ChooseLogoDialog.h
)
set(CAX_HEADERS
    src/Export/CairoxPdf.cpp
)
set(AEIB_HEADERS
    src/Export/AeibPdf.h
)

# Définition des directives de préprocesseur pour chaque exécutable
set(REAX_DEFINES "-DREAX")
set( MAF_DEFINES "-DMAF" )
set( CAX_DEFINES "-DCAX" )
set(AEIB_DEFINES "-DAEIB")

#Icons
set(REAX_ICON_FILES data/IconREAX.rc)
set( MAF_ICON_FILES data/IconMAF.rc )
set( CAX_ICON_FILES data/IconCAX.rc )
set(AEIB_ICON_FILES data/IconAEIB.rc)

# Links
find_package(Qt6 COMPONENTS Core Gui Widgets LinguistTools Test REQUIRED)
include_directories(${Qt6Widgets_INCLUDE_DIRS} ${Qt6Gui_PRIVATE_INCLUDE_DIRS} ${Qt6Gui_INCLUDE_DIRS})
include_directories( Haru/include)
include_directories(QXlsx/include)

set(PUBLIC_LINK
    Qt6::Core
    Qt6::Gui
    Qt6::GuiPrivate
    Qt6::Widgets
    ../Haru/lib/hpdf
    Ole32.lib
    Urlmon.lib
    Wininet.lib 
)

#Ressource Qt
set(QT_RESOURCE_FLAGS "-threshold" "20" "-compress" "9")

set(SOURCES_TRANSLATE
    ${CAX_SOURCES}
    ${MAF_SOURCES}
    ${REAX_SOURCES}
    ${AEIB_SOURCES}
    ${APP_SOURCES}
    ${COMMON_SOURCES}
    ${COMMON_HEADERS}
    ${COMMON_FORMS}
    ${MAF_FORMS}
    ${CAX_FORMS}
)

qt6_add_big_resources(REAX_RESOURCE_FILES data/RessourceREAX.qrc OPTIONS ${QT_RESOURCE_FLAGS})
qt6_add_big_resources( MAF_RESOURCE_FILES data/RessourceMAF.qrc  OPTIONS ${QT_RESOURCE_FLAGS})
qt6_add_big_resources( CAX_RESOURCE_FILES data/RessourceCAX.qrc  OPTIONS ${QT_RESOURCE_FLAGS})
qt6_add_big_resources(AEIB_RESOURCE_FILES data/RessourceAEIB.qrc OPTIONS ${QT_RESOURCE_FLAGS})

set_target_properties(big_resources_RessourceREAX PROPERTIES FOLDER "Ressources")
set_target_properties(big_resources_RessourceMAF  PROPERTIES FOLDER "Ressources")
set_target_properties(big_resources_RessourceCAX  PROPERTIES FOLDER "Ressources")
set_target_properties(big_resources_RessourceAEIB PROPERTIES FOLDER "Ressources")
set_target_properties(rcc_object_RessourceREAX    PROPERTIES FOLDER "Ressources")
set_target_properties(rcc_object_RessourceMAF     PROPERTIES FOLDER "Ressources")
set_target_properties(rcc_object_RessourceCAX     PROPERTIES FOLDER "Ressources")
set_target_properties(rcc_object_RessourceAEIB    PROPERTIES FOLDER "Ressources")

qt6_wrap_ui(COMMON_SOURCES  ${COMMON_FORMS})
qt6_wrap_ui(   MAF_SOURCES     ${MAF_FORMS})
qt6_wrap_ui(   CAX_SOURCES     ${CAX_FORMS})



# Traductions
FILE(GLOB TS_FILES "${CMAKE_CURRENT_SOURCE_DIR}/data/Traduction/*.ts")
set_source_files_properties(${TS_FILES} PROPERTIES OUTPUT_LOCATION "${CMAKE_CURRENT_SOURCE_DIR}/data/Traduction")
add_custom_target(TRSources DEPENDS ${SOURCES_TRANSLATE})
qt6_add_lupdate(TRSources TS_FILES ${TS_FILES} SOURCES ${SOURCES_TRANSLATE} NO_GLOBAL_TARGET)
target_sources(TRSources_lupdate PRIVATE ${TS_FILES})
qt6_add_lrelease(TRSources TS_FILES ${TS_FILES} NO_GLOBAL_TARGET)

set_target_properties(TRSources_lupdate  PROPERTIES FOLDER "Translations")
set_target_properties(TRSources_lrelease PROPERTIES FOLDER "Translations")
set_target_properties(TRSources          PROPERTIES FOLDER "Translations")



# Création des libs
add_library(ReaxLib ${COMMON_HEADERS} ${COMMON_SOURCES} ${REAX_SOURCES} ${REAX_HEADERS} ${REAX_RESOURCE_FILES} ${QM_FILES})
target_compile_definitions(ReaxLib PUBLIC ${REAX_DEFINES})
set_target_properties(ReaxLib PROPERTIES FOLDER "Libs")
target_link_libraries(ReaxLib PUBLIC ${PUBLIC_LINK} debug ../QXlsx/lib/QXlsxQt6d optimized ../QXlsx/lib/QXlsxQt6)
add_dependencies(ReaxLib TRSources)

add_library(MafLib ${COMMON_HEADERS} ${COMMON_SOURCES} ${MAF_SOURCES} ${MAF_HEADERS} ${MAF_RESOURCE_FILES} ${QM_FILES})
target_compile_definitions(MafLib PUBLIC ${MAF_DEFINES})
set_target_properties(MafLib PROPERTIES FOLDER "Libs")
target_link_libraries(MafLib PUBLIC ${PUBLIC_LINK} debug ../QXlsx/lib/QXlsxQt6d optimized ../QXlsx/lib/QXlsxQt6)
add_dependencies(MafLib TRSources)

add_library(CaxLib ${COMMON_HEADERS} ${COMMON_SOURCES} ${CAX_SOURCES} ${CAX_HEADERS} ${CAX_RESOURCE_FILES} ${QM_FILES})
target_compile_definitions(CaxLib PUBLIC ${CAX_DEFINES})
set_target_properties(CaxLib PROPERTIES FOLDER "Libs")
target_link_libraries(CaxLib PUBLIC ${PUBLIC_LINK} debug ../QXlsx/lib/QXlsxQt6d optimized ../QXlsx/lib/QXlsxQt6)
add_dependencies(CaxLib TRSources)

add_library(AeibLib ${COMMON_HEADERS} ${COMMON_SOURCES} ${AEIB_SOURCES} ${AEIB_HEADERS} ${AEIB_RESOURCE_FILES} ${QM_FILES})
target_compile_definitions(AeibLib PUBLIC ${AEIB_DEFINES})
set_target_properties(AeibLib PROPERTIES FOLDER "Libs")
target_link_libraries(AeibLib PUBLIC ${PUBLIC_LINK} debug ../QXlsx/lib/QXlsxQt6d optimized ../QXlsx/lib/QXlsxQt6)
add_dependencies(AeibLib TRSources)



# Création des exécutables
add_executable(Reax ${APP_SOURCES} ${REAX_ICON_FILES})
target_compile_definitions(Reax PRIVATE ${REAX_DEFINES})
set_target_properties(Reax PROPERTIES OUTPUT_NAME "REAX"              FOLDER "Apps")
target_link_libraries(Reax PRIVATE ${CMAKE_CURRENT_BINARY_DIR}/$<CONFIG>/ReaxLib.lib ${PUBLIC_LINK} debug ../QXlsx/lib/QXlsxQt6d optimized ../QXlsx/lib/QXlsxQt6)
add_dependencies(Reax ReaxLib)

add_executable(Maf ${APP_SOURCES} ${MAF_ICON_FILES})
target_compile_definitions(Maf PRIVATE ${MAF_DEFINES})
set_target_properties(Maf PROPERTIES OUTPUT_NAME "MyAxialFan"         FOLDER "Apps")
target_link_libraries(Maf PRIVATE ${CMAKE_CURRENT_BINARY_DIR}/$<CONFIG>/MafLib.lib ${PUBLIC_LINK} debug ../QXlsx/lib/QXlsxQt6d optimized ../QXlsx/lib/QXlsxQt6)
add_dependencies(Maf MafLib)

add_executable(Cax ${APP_SOURCES} ${CAX_ICON_FILES})
target_compile_definitions(Cax PRIVATE ${CAX_DEFINES})
set_target_properties(Cax PROPERTIES OUTPUT_NAME "CAX-fan"            FOLDER "Apps")
target_link_libraries(Cax PRIVATE ${CMAKE_CURRENT_BINARY_DIR}/$<CONFIG>/CaxLib.lib ${PUBLIC_LINK} debug ../QXlsx/lib/QXlsxQt6d optimized ../QXlsx/lib/QXlsxQt6)
add_dependencies(Cax CaxLib)

add_executable(Aeib ${APP_SOURCES} ${AEIB_ICON_FILES})
target_compile_definitions(Aeib PRIVATE ${AEIB_DEFINES})
set_target_properties(Aeib PROPERTIES OUTPUT_NAME "Helicoid-selector" FOLDER "Apps")
target_link_libraries(Aeib PRIVATE ${CMAKE_CURRENT_BINARY_DIR}/$<CONFIG>/AeibLib.lib ${PUBLIC_LINK} debug ../QXlsx/lib/QXlsxQt6d optimized ../QXlsx/lib/QXlsxQt6)
add_dependencies(Aeib AeibLib)



# Deploy
add_custom_target(INSTALL_DLL ${Qt6_DIR}/../../../bin/windeployqt.exe $<CONFIG> --no-translations --no-system-d3d-compiler --no-opengl-sw --no-network --no-svg $<IF:$<CONFIG:Debug>,--pdb,>)
add_custom_command(TARGET INSTALL_DLL PRE_BUILD COMMAND ${CMAKE_COMMAND} -E copy ${CMAKE_CURRENT_SOURCE_DIR}/LicenseLGPL.txt $<CONFIG>/LicenseLGPL.txt)
add_custom_command(TARGET INSTALL_DLL PRE_BUILD COMMAND ${CMAKE_COMMAND} -E copy_directory ${CMAKE_CURRENT_SOURCE_DIR}/Haru/dll $<CONFIG>)
add_custom_command(TARGET INSTALL_DLL PRE_BUILD COMMAND ${CMAKE_COMMAND} -E remove_directory $<CONFIG>/imageFormats)



# Setup
find_path(WIX_ROOT "Wix Path")
configure_file(setups/SetupREAX.wxs.in src/setups/SetupREAX.wxs)
add_custom_target(REAX_SETUP COMMAND "${WIX_ROOT}/candle.exe" -nologo -arch x64 -ext "../WixUtilExtension.dll" -out "${PROJECT_BINARY_DIR}/setup/reax.wixobj" "${PROJECT_BINARY_DIR}/src/setups/SetupREAX.wxs"
COMMAND "${WIX_ROOT}/light.exe" -nologo -ext "../WixUtilExtension.dll" -ext "WixUIExtension" -out "${PROJECT_BINARY_DIR}/setup/REAX_setup${REAX_VERSION}.msi" "${PROJECT_BINARY_DIR}/setup/reax.wixobj")
add_custom_command(TARGET REAX_SETUP POST_BUILD COMMAND ${CMAKE_COMMAND} -E chdir "${PROJECT_BINARY_DIR}/setup" "${CMAKE_CURRENT_SOURCE_DIR}/CMakeHelper/cleanSetup.bat")
set_target_properties(REAX_SETUP PROPERTIES FOLDER "Setups")
add_dependencies(REAX_SETUP Reax)

configure_file(setups/SetupMAF.wxs.in src/setups/SetupMAF.wxs)
add_custom_target(MAF_SETUP COMMAND "${WIX_ROOT}/candle.exe" -nologo -arch x64 -ext "../WixUtilExtension.dll" -out "${PROJECT_BINARY_DIR}/setup/maf.wixobj" "${PROJECT_BINARY_DIR}/src/setups/SetupMAF.wxs"
COMMAND "${WIX_ROOT}/light.exe" -nologo -ext "../WixUtilExtension.dll" -ext "WixUIExtension" -out "${PROJECT_BINARY_DIR}/setup/MyAxialFan_setup${MAF_VERSION}.msi" "${PROJECT_BINARY_DIR}/setup/maf.wixobj")
add_custom_command(TARGET MAF_SETUP POST_BUILD COMMAND ${CMAKE_COMMAND} -E chdir "${PROJECT_BINARY_DIR}/setup" "${CMAKE_CURRENT_SOURCE_DIR}/CMakeHelper/cleanSetup.bat")
set_target_properties(MAF_SETUP PROPERTIES FOLDER "Setups")
add_dependencies(MAF_SETUP Maf)

configure_file(setups/SetupCAX.wxs.in src/setups/SetupCAX.wxs)
add_custom_target(CAX_SETUP COMMAND "${WIX_ROOT}/candle.exe" -nologo -arch x64 -ext "../WixUtilExtension.dll" -out "${PROJECT_BINARY_DIR}/setup/cax.wixobj" "${PROJECT_BINARY_DIR}/src/setups/SetupCAX.wxs"
COMMAND "${WIX_ROOT}/light.exe" -nologo -ext "../WixUtilExtension.dll" -ext "WixUIExtension" -out "${PROJECT_BINARY_DIR}/setup/CAX-fan_setup${CAX_VERSION}.msi" "${PROJECT_BINARY_DIR}/setup/cax.wixobj")
add_custom_command(TARGET CAX_SETUP POST_BUILD COMMAND ${CMAKE_COMMAND} -E chdir "${PROJECT_BINARY_DIR}/setup" "${CMAKE_CURRENT_SOURCE_DIR}/CMakeHelper/cleanSetup.bat")
set_target_properties(CAX_SETUP PROPERTIES FOLDER "Setups")
add_dependencies(CAX_SETUP Cax)

configure_file(setups/SetupAEIB.wxs.in src/setups/SetupAEIB.wxs)
add_custom_target(AEIB_SETUP COMMAND "${WIX_ROOT}/candle.exe" -nologo -arch x64 -ext "../WixUtilExtension.dll" -out "${PROJECT_BINARY_DIR}/setup/aeib.wixobj" "${PROJECT_BINARY_DIR}/src/setups/SetupAEIB.wxs"
COMMAND "${WIX_ROOT}/light.exe" -nologo -ext "../WixUtilExtension.dll" -ext "WixUIExtension" -out "${PROJECT_BINARY_DIR}/setup/AEIB-fan_setup${AEIB_VERSION}.msi" "${PROJECT_BINARY_DIR}/setup/aeib.wixobj")
add_custom_command(TARGET AEIB_SETUP POST_BUILD COMMAND ${CMAKE_COMMAND} -E chdir "${PROJECT_BINARY_DIR}/setup" "${CMAKE_CURRENT_SOURCE_DIR}/CMakeHelper/cleanSetup.bat")
set_target_properties(AEIB_SETUP PROPERTIES FOLDER "Setups")
add_dependencies(AEIB_SETUP Aeib)

add_custom_target(BUILD_ALL_SETUP)
add_dependencies(BUILD_ALL_SETUP REAX_SETUP CAX_SETUP MAF_SETUP AEIB_SETUP)

# Tests
set(TEST_LINK Qt6::Test)
set(TEST_SOURCES 
    test/selectortest.cpp
    test/TestUtilities.cpp
)

add_executable(ReaxTest ${TEST_SOURCES})
set_target_properties(ReaxTest PROPERTIES FOLDER "Tests")
target_link_libraries(ReaxTest PRIVATE ${TEST_LINK} ReaxLib)
target_compile_definitions(ReaxTest PRIVATE ${Reax_DEFINES} DATA_TEST="${CMAKE_CURRENT_SOURCE_DIR}/test")
#set_target_properties(ReaxTest PROPERTIES LINK_FLAGS_DEBUG "/SUBSYSTEM:CONSOLE")
add_dependencies(ReaxTest ReaxLib)
add_test(ReaxTest ReaxTest)

add_executable(MafTest ${TEST_SOURCES})
set_target_properties(MafTest PROPERTIES FOLDER "Tests")
target_link_libraries(MafTest PRIVATE ${TEST_LINK} MafLib)
target_compile_definitions(MafTest PRIVATE ${MAF_DEFINES} DATA_TEST="${CMAKE_CURRENT_SOURCE_DIR}/test")
#set_target_properties(MafTest PROPERTIES LINK_FLAGS_DEBUG "/SUBSYSTEM:CONSOLE")
add_dependencies(MafTest MafLib)
add_test(MafTest MafTest)

add_executable(CaxTest ${TEST_SOURCES})
set_target_properties(CaxTest PROPERTIES FOLDER "Tests")
target_link_libraries(CaxTest PRIVATE ${TEST_LINK} CaxLib)
target_compile_definitions(CaxTest PRIVATE ${Cax_DEFINES} DATA_TEST="${CMAKE_CURRENT_SOURCE_DIR}/test")
#set_target_properties(CaxTest PROPERTIES LINK_FLAGS_DEBUG "/SUBSYSTEM:CONSOLE")
add_dependencies(CaxTest CaxLib)
add_test(CaxTest CaxTest)

add_executable(AeibTest ${TEST_SOURCES})
set_target_properties(AeibTest PROPERTIES FOLDER "Tests")
target_link_libraries(AeibTest PRIVATE ${TEST_LINK} AeibLib)
target_compile_definitions(AeibTest PRIVATE ${Aeib_DEFINES} DATA_TEST="${CMAKE_CURRENT_SOURCE_DIR}/test")
#set_target_properties(AeibTest PROPERTIES LINK_FLAGS_DEBUG "/SUBSYSTEM:CONSOLE")
add_dependencies(AeibTest AeibLib)
add_test(AeibTest AeibTest)

add_custom_target(BUILD_TESTS)
add_dependencies(BUILD_TESTS ReaxTest MafTest CaxTest AeibTest)
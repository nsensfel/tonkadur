## Parameters ##################################################################
SRC_DIR ?= ${CURDIR}/src
BIN_DIR ?= ${CURDIR}/bin
LIB_DIR ?= ${CURDIR}/../../lib
TMP_DIR ?= /tmp/tonkadir-json/

TARGET ?= tonkadur_json_export_lib.jar
STANDALONE ?= tonkadur_json_export_standalone.jar

#### Where to get the missing Jar files.
JAR_SOURCE ?= "https://noot-noot.org/jar_dist/"

#### Binaries
###### JAR binary
JAR ?= jar

###### JRE binary
JAVA ?= java

###### JDK binary
JAVAC ?= javac

###### JSON-SIMPLE
JSON_SIMPLE_JAR ?= $(LIB_DIR)/json-simple-1.1.1.jar
TONKADUR_CORE_JAR ?= $(LIB_DIR)/tonkadur_core_standalone.jar

##### Downloader
DOWNLOADER ?= wget

## Parameters Sanity Check #####################################################
ifeq ($(strip $(JAVA)),)
$(error No Java executable defined as parameter.)
endif

ifeq ($(strip $(JAVAC)),)
$(error No Java compiler defined as parameter.)
endif

ifeq ($(strip $(JSON_SIMPLE_JAR)),)
$(error No JSON_SIMPLE_JAR defined as parameter.)
endif

ifeq ($(strip $(TONKADUR_CORE_JAR)),)
$(error No TONKADUR_CORE_JAR defined as parameter.)
endif

## Java Config #################################################################
ifeq ($(strip $(CLASSPATH)),)
CLASSPATH = "$(SRC_DIR):$(BIN_DIR):$(JSON_SIMPLE_JAR):$(TONKADUR_CORE_JAR)"
else
CLASSPATH = "$(CLASSPATH):$(SRC_DIR):$(BIN_DIR):$(JSON_SIMPLE_JAR):$(TONKADUR_CORE_JAR)"
endif

MANIFEST ?= $(SRC_DIR)/Manifest.txt

## Makefile Magic ##############################################################
JAVA_NAMED_FILES = $(shell find $(SRC_DIR) -iname \*.java)
JAVA_SOURCES = $(JAVA_NAMED_FILES)
CLASSES = $(patsubst $(SRC_DIR)/%.java,$(BIN_DIR)/%.class,$(JAVA_SOURCES))

## Makefile Rules ##############################################################
$(STANDALONE): $(TMP_DIR) $(TARGET) $(JSON_SIMPLE_JAR) $(TONKADUR_CORE_JAR)
	unzip -d $(TMP_DIR) -uo $(TARGET)
	unzip -d $(TMP_DIR) -uo $(TONKADUR_CORE_JAR)
	unzip -d $(TMP_DIR) -uo $(JSON_SIMPLE_JAR)
	$(JAR) -cvfm $@ $(MANIFEST) -C $(TMP_DIR) .

$(TARGET): $(JAVA_SOURCES) $(CLASSES) $(MANIFEST)
	rm -f $(TARGET) $(INSTALL_DIR)/$@
	$(JAR) cf $@ -C $(BIN_DIR) .

clean:
	rm -rf $(BIN_DIR)/*
	rm -rf $(STANDALONE) $(TARGET)
	rm -rf $(LIB_DIR)/$(STANDALONE)

# Pattern rules can be used to generate multiple target in a single action.
$(CLASSES): $(BIN_DIR)/%.class: $(SRC_DIR)/%.java $(BIN_DIR) $(JSON_SIMPLE_JAR) $(TONKADUR_CORE_JAR)
	$(JAVAC) -cp $(CLASSPATH) -d $(BIN_DIR) $<

%.jar:
	$(MAKE) $(LIB_DIR)
	echo "Attempting to download missing jar '$@'..."
	cd $(LIB_DIR); $(DOWNLOADER) "$(JAR_SOURCE)/$(notdir $@)"

$(TMP_DIR):
	mkdir -p $@

$(LIB_DIR):
	mkdir -p $@

$(BIN_DIR):
	mkdir -p $@

##### For my private use...
publish: $(STANDALONE)
	scp $^ dreamhost:~/noot-noot/jar_dist/

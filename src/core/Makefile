## Parameters ##################################################################
SRC_DIR ?= ${CURDIR}/src
BIN_DIR ?= ${CURDIR}/bin
LIB_DIR ?= ${CURDIR}/../../lib
TMP_DIR ?= /tmp/tonkadir/

TARGET ?= tonkadur_core_lib.jar
STANDALONE ?= tonkadur_core_standalone.jar
INSTALL_DIR ?= ${CURDIR}

#### Where to get the missing Jar files.
JAR_SOURCE ?= "https://noot-noot.org/jar_dist/"

#### Binaries
###### JAR binary
JAR ?= jar

###### JRE binary
JAVA ?= java

###### JDK binary
JAVAC ?= javac

###### ANTLR
ANTLR_JAR ?= $(LIB_DIR)/antlr-4.7-complete.jar

##### Downloader
DOWNLOADER ?= wget

## Parameters Sanity Check #####################################################
ifeq ($(strip $(JAVA)),)
$(error No Java executable defined as parameter.)
endif

ifeq ($(strip $(JAVAC)),)
$(error No Java compiler defined as parameter.)
endif

ifeq ($(strip $(ANTLR_JAR)),)
$(error No ANTLR_JAR defined as parameter.)
endif

## Java Config #################################################################
ifeq ($(strip $(CLASSPATH)),)
CLASSPATH = "$(SRC_DIR):$(BIN_DIR):$(ANTLR_JAR)"
else
CLASSPATH = "$(CLASSPATH):$(SRC_DIR):$(BIN_DIR):$(ANTLR_JAR)"
endif

MANIFEST ?= $(SRC_DIR)/Manifest.txt

## Makefile Magic ##############################################################
JAVA_NAMED_FILES = $(shell find $(SRC_DIR) -iname \*.java)
ANTLR_SOURCES = $(shell find $(SRC_DIR) -iname \*.g4)
ANTLR_JAVA_SOURCES = $(ANTLR_SOURCES:.g4=.java)
JAVA_SOURCES = \
	$(filter-out $(ANTLR_JAVA_SOURCES), $(JAVA_NAMED_FILES)) \
	$(ANTLR_JAVA_SOURCES)
CLASSES = $(patsubst $(SRC_DIR)/%.java,$(BIN_DIR)/%.class,$(JAVA_SOURCES))

## Makefile Rules ##############################################################
$(STANDALONE): $(TMP_DIR) $(TARGET) $(ANTLR_JAR)
	unzip -d $(TMP_DIR) -uo $(TARGET)
	unzip -d $(TMP_DIR) -uo $(ANTLR_JAR)
	$(JAR) -cvfm $@ $(MANIFEST) -C $(TMP_DIR) .

ifeq ($(INSTALL_DIR),${CURDIR})
$(TARGET): $(ANTLR_JAR) $(JAVA_SOURCES) $(CLASSES) $(MANIFEST)
	rm -f $(TARGET) $(INSTALL_DIR)/$@
	$(JAR) cf $@ -C $(BIN_DIR) .
else
$(TARGET): $(ANTLR_JAR) $(JAVA_SOURCES) $(CLASSES) $(MANIFEST)
	rm -f $(TARGET) $(INSTALL_DIR)/$@
	$(JAR) cf $@ -C $(BIN_DIR) .
	cp -f $@ $(INSTALL_DIR)/$@
endif

clean:
	rm -rf $(filter-out $(ANTLR_SOURCES),$(wildcard $(ANTLR_SOURCES:.g4=*)))
	rm -rf $(BIN_DIR)/*
	rm -rf $(TARGET) $(STANDALONE)

# Pattern rules can be used to generate multiple target in a single action.
%Lexer.java %Parser.java: $(ANTLR_SOURCES)
	$(JAVA) -jar $(ANTLR_JAR) -lib $(dir $@) $^

$(CLASSES): $(BIN_DIR)/%.class: $(SRC_DIR)/%.java $(BIN_DIR)
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
publish: $(TARGET) $(STANDALONE)
	scp $^ dreamhost:~/noot-noot/jar_dist/

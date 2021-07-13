TONKADUR_COMPILER ?= java -jar /my/src/tonkadur/src/json-export/tonkadur_json_export_standalone.jar
TONKADUR_INTERPRETER ?= python3 /my/src/tonkadur-python-engine/tonkadur_ui.py -f

FATE_FILES ?= $(wildcard ${CURDIR}/*.fate)
JSON_FILES ?= $(patsubst %.fate,%.fate.json,$(FATE_FILES))
RESULT_FILES ?= $(patsubst %.fate,%.txt,$(FATE_FILES))

default: $(RESULT_FILES)
	@echo "########"
	@echo "Non-Completed tests:"
	@grep -L -r COMPLETED $(RESULT_FILES) || true
	@echo "########"
	@echo "Failed tests:"
	@grep -r FAILED $(RESULT_FILES) || true

clean:
	rm -f $(JSON_FILES) $(RESULT_FILES)

$(RESULT_FILES): %.txt: %.fate.json
	-$(TONKADUR_INTERPRETER) $< > $@

$(JSON_FILES): %.fate.json: %.fate
	-$(TONKADUR_COMPILER) $<

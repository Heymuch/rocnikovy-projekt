# JDK z lokálního adresáře .jdks (při více verzích se použije nejnovější),
# lze přepsat: make run JAVA_HOME=/cesta/k/jdk
GRADLE = JAVA_HOME=$(CURDIR)/.jdk $(CURDIR)/gradlew

.PHONY: assemble
assemble:
	$(GRADLE) assemble;

.PHONY: run
run:
	$(GRADLE) run;

.PHONY: format
format:
	$(GRADLE) ktlintFormat;

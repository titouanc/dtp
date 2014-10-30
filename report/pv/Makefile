MDFILES = $(shell echo *.md)

all: main.tex

main.tex: $(subst .md,.tex,${MDFILES})
	cat $^ > $@

%.tex: %.md
	lunamark -t latex < $< > "$@"
	echo "\\\\label{$<}"

.PHONY: clean all
clean:
	rm -f *.tex

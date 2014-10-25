MDFILES = $(shell echo *.md)

all: $(subst .md,.tex,${MDFILES})

%.tex: %.md
	/bin/echo -e "#$(shell echo "$<" | sed 's/\.md//' | tr -s _ ' ')\n\n$$(cat "$<")" | lunamark -t latex > "$@"
	echo "\\\\label{$<}"

.PHONY: clean
clean:
	rm -f *.tex

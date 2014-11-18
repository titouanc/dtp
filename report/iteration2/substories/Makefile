MDFILES = $(shell echo *.md)

all: $(subst .md,.tex,${MDFILES})

%.tex: %.md
	/bin/echo "\\begin{framed}" > "$@"
	/bin/echo -e "##$(shell echo "$<" | sed -E 's/[0-9\.]+_?//' | sed 's/\.md//' | tr -s _ ' ')\n\n$$(cat "$<")" | lunamark -t latex >> "$@"
	/bin/echo "\\label{$<}" >> "$@"
	/bin/echo "\\end{framed}" >> "$@"

.PHONY: clean
clean:
	rm -f *.tex

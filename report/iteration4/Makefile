all: main.tex
	
main.tex:  
	/bin/echo "\\chapter{Itération 4}" > $@
	cat intro.tex >> $@
	cat tasks.tex >> $@
	cat report.tex >> $@
	
.PHONY: pv clean

%.pdf: %.tex
	pdflatex $<

clean:
	rm -f main.tex

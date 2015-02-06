all: main.tex
	
main.tex: substories 
	/bin/echo "\\chapter{Itération 2}" > $@
	cat intro.tex >> $@
	/bin/echo "\\section{Histoires utilisateur}" >> $@
	cat substories/*.tex >> $@
	cat report.tex >> $@
	
.PHONY: pv substories clean

%.pdf: %.tex
	pdflatex $<

substories:
	make -C substories

clean:
	rm -f main.tex stories.tex stories.pdf stories.log stories.aux
	make -C substories clean

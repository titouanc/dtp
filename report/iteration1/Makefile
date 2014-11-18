main.tex: substories libs
	/bin/echo "\\chapter{Itération 1}"> $@
	cat intro.tex >> $@
	/bin/echo "\\section{Librairies introduites}" >> $@
	cat libs/*.tex >> $@

.PHONY: pv substories libs clean

%.pdf: %.tex
	pdflatex $<

substories:
	make -C substories

libs:
	make -C libs

clean:
	rm -f main.tex stories.tex stories.pdf stories.log stories.aux
	make -C substories clean
	make -C libs clean

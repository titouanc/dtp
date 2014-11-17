main.tex: substories 
	/bin/echo "\\chapter{Itération 2}"> $@
	
.PHONY: pv substories clean

%.pdf: %.tex
	pdflatex $<

substories:
	make -C substories

clean:
	rm -f main.tex stories.tex stories.pdf stories.log stories.aux
	make -C substories clean

main.tex: substories 
	/bin/echo "\\chapter{Itération 2}"> $@
	cat intro.tex >> $@
	/bin/echo "\\section{Histoires utilisateur}" >> $@
	cat substories/*.tex >> $@

.PHONY: pv substories clean

%.pdf: %.tex
	pdflatex $<

stories.tex: substories
	/bin/echo "\\documentclass[a4paper]{article} \\usepackage{framed} \\usepackage{fullpage} \\usepackage[french]{babel} \\usepackage[utf8]{inputenc} \\usepackage[T1]{fontenc}" > $@
	/bin/echo "\\begin{document} \\section{Histoires pour l'itération 2}" >> $@
	cat substories/*.tex >> $@
	/bin/echo "\\end{document}" >> $@

substories:
	make -C substories

clean:
	rm -f main.tex stories.tex stories.pdf stories.log stories.aux
	make -C substories clean

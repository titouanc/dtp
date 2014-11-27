all: report.pdf stories.pdf meetings.pdf

report.pdf: report.tex iteration1 iteration2
	pdflatex $<
	# ToC
	pdflatex $<

.PHONY: iteration1 iteration2 clean pv

iteration1:
	make -C $@

iteration2:
	make -C $@

stories.pdf: stories.tex
	pdflatex $<

stories.tex: iteration1 iteration2
	/bin/echo "\\documentclass[a4paper]{article} \\usepackage{framed} \\usepackage{fullpage} \\usepackage[french]{babel} \\usepackage[utf8]{inputenc} \\usepackage[T1]{fontenc}" > $@
	/bin/echo "\\title{Histoires utilisateur} \\author{Groupe 3} \\begin{document} \\maketitle" >> $@
	for f in $^; do echo "\section{$$(echo $$f | sed -E 's/iteration([0-9])/Itération \1/')}" >> $@; cat $$f/substories/*.tex >> $@; done
	/bin/echo "\\end{document}" >> $@

meetings.pdf: meetings.tex
	pdflatex $<
	#ToC
	pdflatex $<

meetings.tex: pv
	/bin/echo "\\documentclass[a4paper]{article} \\usepackage{framed} \\usepackage{fullpage} \\usepackage[french]{babel} \\usepackage[utf8]{inputenc} \\usepackage[T1]{fontenc}" > $@
	/bin/echo "\\title{PV des réunions} \\author{Groupe 3} \\begin{document} \\maketitle \\tableofcontents" >> $@
	cat pv/main.tex >> $@
	/bin/echo "\\end{document}" >> $@

pv:
	make -C $@

clean:
	rm -f report.aux report.log stories.*
	make -C iteration1 clean
	make -C iteration2 clean
	make -C pv clean

all: report.pdf stories.pdf meetings.pdf

report.pdf: report.tex iteration1 iteration2 iteration3 iteration4
	pdflatex $<
	# ToC
	pdflatex $<

.PHONY: iteration1 iteration2 iteration3 iteration4 clean pv

iteration1:
	make -C $@

iteration2:
	make -C $@

iteration3:
	make -C $@

iteration4:
	make -C $@

stories.pdf: stories.tex
	pdflatex $<


meetings.pdf: meetings.tex
	pdflatex $<
	#ToC
	pdflatex $<

meetings.tex: pv
	/bin/echo "\\documentclass[a4paper]{article} \\usepackage{framed} \\usepackage{fullpage} \\usepackage[french]{babel} \\usepackage[utf8]{inputenc} \\usepackage[T1]{fontenc} \\usepackage{float}" > $@
	/bin/echo "\\title{PV des réunions} \\author{Groupe 3} \\begin{document} \\maketitle \\tableofcontents" >> $@
	cat pv/main.tex >> $@
	/bin/echo "\\end{document}" >> $@

pv:
	make -C $@

clean:
	rm -f report.aux report.log stories.aux stories.log stories.pdf
	make -C iteration1 clean
	make -C iteration2 clean
	make -C iteration3 clean
	make -C iteration4 clean
	make -C pv clean

report.pdf: report.tex iteration1 pv
	pdflatex $<

.PHONY: iteration1 clean pv
iteration1:
	make -C $@

pv:
	make -C $@

clean:
	rm -f report.aux report.log
	make -C iteration1 clean
	make -C pv clean

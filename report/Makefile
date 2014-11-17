report.pdf: report.tex iteration1 iteration2 pv
	pdflatex $<

.PHONY: iteration1 iteration2 clean pv
iteration1:
	make -C $@

iteration2:
	make -C $@

pv:
	make -C $@

clean:
	rm -f report.aux report.log
	make -C iteration1 clean
	make -C iteration2 clean
	make -C pv clean

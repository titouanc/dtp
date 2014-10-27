report.pdf: report.tex iteration1
	pdflatex $<

.PHONY: iteration1 clean
iteration1:
	make -C $@

clean:
	rm report.aux report.log
	make -C iteration1 clean
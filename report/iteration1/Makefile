main.tex: substories pv
	/bin/echo "\\chapter{Itération 1}\\section{Histoires utilisateur}" > $@
	cat substories/*.tex >> $@
	/bin/echo "\\appendix\\chapter{PV des réunions}" >> $@
	cat pv/*.tex >> $@

.PHONY: pv substories clean

substories:
	make -C substories

pv:
	make -C pv

clean:
	rm -f main.tex
	make -C substories clean
	make -C pv clean

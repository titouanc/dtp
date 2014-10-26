main.tex: substories pv libs
	/bin/echo "\\chapter{Itération 1}"> $@
	cat intro.tex >> $@
	/bin/echo "\\section{Histoires utilisateur}" >> $@
	cat substories/*.tex >> $@
	/bin/echo "\\section{Librairies introduites}" >> $@
	cat libs/*.tex >> $@
	/bin/echo "\\appendix\\chapter{PV des réunions}" >> $@
	cat pv/*.tex >> $@

.PHONY: pv substories libs clean

substories:
	make -C substories

pv:
	make -C pv

libs:
	make -C libs

clean:
	rm -f main.tex
	make -C substories clean
	make -C pv clean
	make -C libs clean

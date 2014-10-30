main.tex: substories libs
	/bin/echo "\\chapter{Itération 1}"> $@
	cat intro.tex >> $@
	/bin/echo "\\section{Histoires utilisateur}" >> $@
	cat substories/*.tex >> $@
	/bin/echo "\\section{Librairies introduites}" >> $@
	cat libs/*.tex >> $@

.PHONY: pv substories libs clean

substories:
	make -C substories

libs:
	make -C libs

clean:
	rm -f main.tex
	make -C substories clean
	make -C libs clean

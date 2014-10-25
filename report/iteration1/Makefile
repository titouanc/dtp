main.tex: substories
	echo "\\\\chapter{Itération 1} \\\\section{Histoires utilisateur}" > $@
	cat substories/*.tex >> $@

.PHONY: substories clean

substories:
	make -C substories

clean:
	rm -f main.tex
	make -C substories clean

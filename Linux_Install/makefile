COMP = javac
FROM = ../sikla
DEST = $(HOME)/.sikla/sikla

install:
	mkdir -p $(DEST)/profiles
	cp -f $(FROM)/*.class $(DEST)
	cp $(FROM)/stages $(DEST)
	cp -r $(FROM)/data/ $(DEST)
	cp -f Sikla $(HOME)


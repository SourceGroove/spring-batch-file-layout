git commit -a -m "Pre release" && git push

mvn -B release:prepare release:perform

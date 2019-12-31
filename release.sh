mvn clean
mvn -B release:prepare release:perform

git push -–tags
git push origin master
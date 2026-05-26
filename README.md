# Installasjon
Prosjektet er tilgjengelig på GitHub og krever Java 21 og Apache Maven for å kjøres.
Klon først repositoriet og naviger inn i prosjektmappen:

~~~
git clone https://github.com/HeyItsAp/mappeVurdering2
cd mappeVurdering2
~~~
Bygg deretter prosjektet og kjør applikasjonen med følgende Maven-kommando:

~~~
mvn javafx:run
~~~

For å kjøre enhetstestene separat kan følgende kommando benyttes:
~~~
mvn test
~~~

Det er også mulig å bygge og pakke prosjektet til en .jar-fil
~~~
mvn clean package
~~~

Merk at prosjektet krever en internettforbindelse ved første bygg, da Maven laster ned nødvendige avhengigheter automatisk fra Maven Central.

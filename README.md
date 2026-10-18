# TP2 - Les sockets TCP

Projet de developpement d'applications reparties (DAR).

## Activite 2-1 - Echange de chaines de caracteres

Dans cette premiere activite, le client et le serveur echangent une expression
complete sous forme de texte avec `DataInputStream` et `DataOutputStream`.

Le client valide une addition comme `55 + 25`, l'envoie au serveur et affiche
le resultat recu. Les expressions invalides ne sont pas envoyees.

### Captures d'ecran

#### Client - Activite 2-1

![Client - Activite 2-1](screenshots/client1.png)

#### Serveur - Activite 2-1

![Serveur - Activite 2-1](screenshots/server1.png)

---

## Activite 2-2 - Echange d'objets Java

Cette deuxieme activite etend la precedente avec l'echange d'objets Java
serialises entre le client et le serveur.

### Objectifs de l'activite

- Creer une classe Java qui implemente `Serializable`.
- Envoyer du client au serveur un objet contenant deux operandes et un operateur.
- Recuperer les donnees de l'objet cote serveur et effectuer le calcul.
- Renvoyer un objet contenant le resultat au client.
- Lire et afficher le resultat cote client.

### Structure

```text
.
|-- src/
|   |-- clientPackage/
|   |   |-- CalculationResponse.java
|   |   |-- Client.java
|   |   `-- OperationRequest.java
|   |-- serverPackage/
|   `-- Server.java
|-- screenshots/
|-- README.md
`-- .gitignore
```

`OperationRequest` et `CalculationResponse` implementent `Serializable` et
constituent le protocole d'echange entre le client et le serveur. Le serveur
importe ces deux classes depuis `clientPackage` afin que les deux programmes
utilisent exactement les memes noms de classes serialisees.

```text
Socket
  -> BufferedInputStream
  -> ObjectInputStream
```

`BufferedInputStream` met en memoire tampon les donnees recues. Les flux objet
serialisent et deserialisent les instances Java.

### Fonctionnement

1. Le client analyse une expression de la forme `nombre operateur nombre`.
2. Le client construit et serialise un `OperationRequest`.
3. Le serveur deserialise la requete, recupere les trois valeurs et effectue
   le calcul.
4. Le serveur serialise un `CalculationResponse`.
5. Le client deserialise la reponse et l'affiche.

Les operateurs pris en charge sont `+`, `-`, `*` et `/`. La division par zero
est refusee par le serveur. Les expressions invalides sont refusees par le
client et ne sont pas envoyees.

### Compilation

Depuis la racine du projet :

```powershell
javac -d out (Get-ChildItem -Recurse -Filter *.java src).FullName
```

La commande equivalente, compatible avec un terminal qui accepte les
wildcards Java, est :

```powershell
javac -d out src\clientPackage\*.java src\serverPackage\Server.java
```

### Execution

### Serveur

Dans un premier terminal :

```powershell
java -cp out serverPackage.Server 5000
```

Le port par defaut est `5000`.

### Client

Dans un second terminal :

```powershell
java -cp out clientPackage.Client localhost 5000
```

L'hote par defaut est `localhost` et le port par defaut est `5000`.

Saisir ensuite, par exemple :

```text
55 + 25
20 - 7
6 * 8
15 / 4
```

Le client affiche respectivement :

```text
55 + 25 = 80
20 - 7 = 13
6 * 8 = 48
15 / 4 = 3.75
```

Saisir `exit` pour fermer proprement la session client.

### Tests d'erreur

Les saisies `25 ** 55`, `abc + 3` et `10 / 0` sont gerees sans arret du
serveur :

- les deux premieres sont refusees par le client ;
- la derniere est refusee par le serveur avec le message
  `Division par zero impossible.`

### Captures d'ecran

#### Client - Activite 2-2

Le client envoie une requete serialisee et affiche le resultat recu du
serveur :

![Client - Activite 2-2](screenshots/client2.png)

#### Serveur - Activite 2-2

Le serveur recoit l'objet, effectue le calcul et affiche le resultat :

![Serveur - Activite 2-2](screenshots/server2.png)

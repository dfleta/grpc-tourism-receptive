Ricksy Business - GRPC
======================

A "tourism" receptive system based on GRPC (Google remote procedure call framework), implementing the observer GoF design pattern, and test driven developed.

## User stories

Based on the plot of the chapter 11th of Rick and Morty first season series.

Rick and Summer throw a party at home. Among the guests, there are teenagers, aliens, Gearhead, Squanchy, Birdpearson and Abradolph Lincler (a DNA mixture of Adolf Hitler and Abraham Lincoln).

When a guest enters the party he /she is processed in the receptive "tourist" system that Rick has developed ad hoc. The guest presents his /her credit card and the system charges:

 - The reservation cost of a UFO to safely return home when the party is over.
 - The cost of a welcome pack, a piece of Collaxion Crystal, Rick's favourite amenities.

## Architecture

Rick is such a huge fan of the **GoF design patterns** book that he desings the system architecture by using the **observer pattern**. Both, the UFO reservation component and the crystal expender component, observe the receptive component, therefore when a guest is processed by the receptive it charges automatically in the credit card the cost of both services. Of course, these system desing is conform with the **SOLID Open Closed Principle** (also respects **Dependency Inversion** Principle), so it is not necessary to modify the existing code to add and enable a new service component.

As Rick find himself bored till the party stats, he decides to learn about **gRPC** - a _"modern open source high performance RPC framework by Google"_- and develops all the three components (UFOs park, crystal expender and payment services) running in separate **microservices**.

## Testing

Rick has written the code with tons of fury and caffeine but, quoting himself _"I programmed you to believe that"_, is the **TDD** that saves the day, . **Test Driven Development**, paying the **technical debt** every day, is a matter of _wubba lubba dub dub_ concern to him.
**Conventional commits** are preferred too. 


## Installation

Clone the repo and move to the main folder. Then:

```bash
$ ./gradlew clean build
$ ./gradle installDist
```

## Usage

### Start the services:

``` bash
$ ./build/install/grpc-ricksy-business/bin/payment-server
$ ./build/install/grpc-ricksy-business/bin/crystal-expender-server
$ ./build/install/grpc-ricksy-business/bin/ufos-park-server
```

### Run the main app:

Morty is joining the party (not without regret):

```
$ ./build/install/grpc-ricksy-business/bin/app Morty 111111111111
```

Also, Rick friends are welcome too:

``` bash
$ ./build/install/grpc-ricksy-business/bin/app Abradolph_Lincler 4916119711304546

$ ./build/install/grpc-ricksy-business/bin/app Squanchy 4444444444444444

$ ./build/install/grpc-ricksy-business/bin/app Gearhead 8888888888888888
```

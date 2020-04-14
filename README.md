Ricksy Business - gRPC
======================

A "tourism" receptive system based on **gRPC** (Google remote procedure call framework), implementing the **observer GoF design pattern**, and **test driven development (TDD)**, written in **Java**.

By using **protocol buffers** we serialize structured data. gRPC exchange those data between multiple services in different environments, in a **microservices** style architecture.

A project for personal learning and then teaching my students. Some test code from gRPC boilerplate - [gRPC Java Basics](https://grpc.io/docs/tutorials/basic/java/ "gRPC Java Basics") and [grpc Examples](https://github.com/grpc/grpc-java/blob/master/examples/README.md "gRPC examples on github") , testing clients and servers- has been included in the `test.org.elsmancs.grpc` package.

## User Stories

Based on the plot of the chapter 11th [_"Ricksy Business"_](https://rickandmorty.fandom.com/wiki/Ricksy_Business "Ricksy business fandom page")" of Rick and Morty first season series.

Rick and Summer throw a party at home. Among the guests, there are teenagers, aliens, Gearhead, Squanchy, Birdpearson and Abradolf Lincler (a DNA mixture of Adolf Hitler and Abraham Lincoln).

When a guest enters the party he /she is processed in the receptive "tourist" system that Rick has developed _ad hoc_. The guest presents his /her credit card and the system charges:

 - The reservation cost of a UFO to safely return home when the party is over.
 - The cost of a welcome pack, a piece of Kalaxian Crystal, Rick's favourite amenities.

## Architecture

Rick is such a huge fan of the **GoF design patterns** book that he designs the system architecture in accordance with the **observer pattern**. Both, the UFO reservation component and the kalaxian crystal dispenser component, observe the receptive component. Therefore, when a guest is processed by the receptive it charges automatically the guest credit card with the cost of both services. Of course, the system design is conformed with the **SOLID Open Closed Principle** (also respects **Dependency Inversion** Principle), so it is not necessary to modify the existing code to add and enable a new service component.  

As Rick find himself bored till the party starts, he decides to learn about **gRPC** - a _"modern open source high performance RPC framework by Google"_- and develops all the three components (UFOs park, crystal dispenser and payment services) running in separate **microservices** exchanging structured data in the form of **protocol buffers**.

## Testing

Rick has written the code with tons of fury and caffeine but, quoting himself _"I programmed you to believe that"_, is the **TDD** that saves the day. **Test Driven Development**, paying the **technical debt** every day, is a matter of _wubba lubba dub dub_ concern to him. 
**Mockito** and **harmcrest** worth it. 
**Conventional commits** are preferred too.


## Installation

Clone the repo and move to the main folder:

```bash
$ cd ricksy-business-gRPC
```
Then compile clients and servers:

``` bash
$ ./gradlew clean build
$ ./gradle installDist
```

For focus on testing:

``` bash
$ ./gradlew test
```

## Usage

### Start the services:

``` bash
$ ./build/install/grpc-ricksy-business/bin/payment-server
$ ./build/install/grpc-ricksy-business/bin/crystal-dispenser-server
$ ./build/install/grpc-ricksy-business/bin/ufos-park-server
```

### Run the main app:

Morty is joining the party (not without regret):

``` bash
$ ./build/install/grpc-ricksy-business/bin/app Morty 111111111111
```

Also, Rick friends are welcome too:

``` bash
$ ./build/install/grpc-ricksy-business/bin/app Abradolf_Lincler 4916119711304546

$ ./build/install/grpc-ricksy-business/bin/app Squanchy 4444444444444444

$ ./build/install/grpc-ricksy-business/bin/app Gearhead 8888888888888888
```

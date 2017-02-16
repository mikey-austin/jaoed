# jaoed [![Build Status](https://travis-ci.org/mikey-austin/jaoed.svg?branch=master)](https://travis-ci.org/mikey-austin/jaoed) [![Coverage Status](https://coveralls.io/repos/github/mikey-austin/jaoed/badge.svg?branch=master)](https://coveralls.io/github/mikey-austin/jaoed?branch=master)

Java ATA over ethernet storage target based on the now abandoned qaoed project.

Some basic goals are as follows:
* Flexible configuration, motivated by qaoed's config system (ie sysadmin friendly)
* Support as much of the AoE standard as possible
* One thread per managed device
* Utilize libpcap to receive & send the actual ethernet frames
* Make a simple, well-tested and stable storage target that is portable to many systems
* Ability to reload the configuration safely without a restart to facilitate the adding/removing of devices with ease

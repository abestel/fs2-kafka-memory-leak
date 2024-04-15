#!/bin/bash

while sleep 1
do
  jcmd fs2.kafka.MemoryLeaker GC.class_histogram | grep 'cats.effect.CallbackStack$Node';
done
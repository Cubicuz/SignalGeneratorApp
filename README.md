# SignalGeneratorApp
the idea and initial code is from a university project of a fellow student and me. 
I start here again from scratch because i didn't backup the final version, lets see how long the motivation lasts.

Goal is to have a Signal Generator which can be manipulated by the internal Sensors. 
Maybe allow multiple Device setups with mqtt

i use jsyn for doing the audio part so i only have to do the interface

# Feature wishes
 - multiple presets
 - multiple saves

## more intuitive interaction
### input
 - select a sensor
 - set situations for quiet and loud
   - consider that some sensor ranges wrap around, angle for example
### output
 - select a preset
   - continuous wave
   - discrete pulses
 - modify the preset and store it as a user setting
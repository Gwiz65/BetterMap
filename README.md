# BetterMap
A Better Map for Wurm Unlimited

- **BetterMap Features:**
  - Higher resolution isometric server map
  - Map window is resizable and location/size is saved between game sessions
  - Map is zoomable (scroll wheel) and draggable when zoomed in (left click + drag)
  - Provides map markers for Start Towns, Deeds and Soulfall Stones with names displayed on mouseover (if allowed by server owner)
  - Start Towns indicated with a green marker, Deeds indicated with a yellow marker and Soulfall Stones indicated with a blue marker
 
 - **Installation**
   - Needs Ago's Client ModLauncher installed
   - Needs Ago's ServerPack Client mod installed
   - Needs [BetterMapServer mod](https://github.com/Gwiz65/BetterMapServer/releases/latest) installed on the server
   - Extract bettermap-x.x.zip into WurmLauncher directory

- **Starting BetterMap**
  - BetterMap can be toggled on/off from the Main Menu (esc)
  - Bettermap can be toggled  on/off with the console command _bettermap toggle_
  - BetterMap toggle can be bound to a key with _bind (key) "bettermap toggle"_. For example: _bind m "bettermap toggle"_ to bind to the M key
  
- **Map Loading**
  - If BetterMap is opened prior to the serverpack being downloaded you will get a "Missing Image" screen. Simply close and reopen the BetterMap window after the serverpack is downloaded and the map should load itself.
  - On server crossings, closing and reopening the BetterMap window after the new serverpack has been downloaded *should* load the new map . This has not been tested.
 
- **Release Notes:**
  - Release 1.0 - Initial release.
  - Release 1.1 - Fixed conflicts with other mods
  - Release 1.2 - Improved marker accuracy

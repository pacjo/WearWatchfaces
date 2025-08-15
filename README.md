# WearWatchfaces 2.0 - rewrite

## jetpack / WFF shared project

This project contains watchfaces written with both legacy [jetpack libraries](https://developer.android.com/jetpack/androidx/releases/wear-watchface) and newer [WatchFaceFormat](https://developer.android.com/training/wearables/wff).

- legacy watchfaces are available in [`jetpack`](./jetpack)
- WFF based watchfaces are available in [`wff`](./wff)


## TODO:

- [ ] use good practises for android libraries -  https://developer.android.com/studio/projects/android-library
- [ ] allow overriding style defaults per watchface (for example when customization is disabled)
- [ ] add priority to WatchFaceFeature-s to order them consistently in the on watch editor
- [ ] make all ic_launcher resources specify `adaptive` icon
- [ ] make jimball play [Funkytown](https://www.youtube.com/watch?v=Z6dqIYKIBSU) (maybe even configurable: **never**, **on_touch**, **always**)
- [ ] add jimball gif sources (pixelorama project) and figure out how to manage original sources for WFF based watchfaces
- [ ] add nav controller to [`EditorActivity`](./jetpack/feature/editor/src/main/java/nodomain/pacjo/wear/watchface/feature/editor/ui/activities/EditorActivity.kt) and make every feature it's separate screen with the main HorizontalPager only highlighting the configured feature
- [x] fix hands rotation
- [ ] don't draw seconds hand in ambient
- [ ] enable minification where possible
- [ ] separate jetpack and wff based projects by package name
- [ ] figure out something with `android.service.wallpaper.square_mode` metadata
- [ ] figure out how to share resources between jetpack and wff
- [x] backgrounds should get watch state to be able to react to ambient changes, maybe zonedTimeDate too, context?
- [x] maybe don't require overriding all methods in `WatchFaceRenderer`? (but we could log it)
- [ ] add docs to watchfaces
  - [ ] add missing readmes
  - [ ] add images
  - [ ] standardize format
- [ ] convert BackgroundMusicPlayer to proper feature module
- [ ] add TapListener feature
- [ ] allow features to implement multiple (e.g. background can be a background and a taplistener)
- [ ] make drawing from WatchFaceRendererImpl WatchFaceLayer aware
- [x] unify Analog and Digital WatchFaceService-s and RendererAdapter-s
- [ ] maybe :jetpack:watchface:base should depend on :jetpack:feature:background?
- [ ] features should be able to opt out of being user-configurable
- [ ] make :jetpack:watchfaces:snake use new Grid2d class
- [ ] redo previews
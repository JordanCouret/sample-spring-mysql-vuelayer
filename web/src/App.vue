<template xmlns:>
  <div id="app" :class="[$options.name]">
    <!-- app map -->
    <vl-map v-if="mapVisible" class="map" ref="map" :load-tiles-while-animating="true" :load-tiles-while-interacting="true"
            @click="clickCoordinate = $event.coordinate"
            data-projection="EPSG:4326" @mounted="onMapMounted">
      <!-- map view aka ol.View -->
      <vl-view ref="view" :center.sync="center" :zoom.sync="zoom" :rotation.sync="rotation"></vl-view>

      <!-- interactions -->
      <vl-interaction-select :features.sync="selectedFeatures">
        <template slot-scope="select">
          <!-- selected feature popup -->
          <vl-overlay class="feature-popup" v-for="feature in select.features" :key="feature.id" :id="feature.id"
                      :position="pointOnSurface(feature.geometry)" :auto-pan="true" :auto-pan-animation="{ duration: 300 }">
            <template slot-scope="popup">
              <section class="card">
                <div class="card-header">
                  <p class="card-header-title">
                    {{ feature.properties.name }}
                  </p>
                  <a class="card-header-icon" title="Close"
                     @click="selectedFeatures = selectedFeatures.filter(f => f.id !== feature.id)">
                    <b-icon icon="close"></b-icon>
                  </a>
                </div>
                <div class="card-content">
                  <div class="content">
                    <p><strong>Address : </strong> {{ feature.properties.address}}</p>
                    <p><strong>Phone : </strong> {{ feature.properties.phone}}</p>
                    <p><strong>Web : </strong> <a :href="feature.properties.webSite" target="_blank" rel="noopener">{{ feature.properties.webSite}}</a></p>
                  </div>
                </div>
              </section>
            </template>
          </vl-overlay>
          <!--// selected popup -->
        </template>
      </vl-interaction-select>
      <!--// interactions -->


      <!-- overlay marker with animation -->
      <vl-feature v-for="item in items" :key="item.id" :id="item.id" ref="marker" :properties="{ id: item.id, name: item.name, address: item.address, phone: item.phone, webSite: item.webSite }">
          <vl-geom-point :coordinates="[item.point.coordinates[1], item.point.coordinates[0]]"></vl-geom-point>

          <vl-style-box>
            <vl-style-icon src="./assets/marker.png" :scale="0.4" :anchor="[0.15, 0.95]" :size="[128, 128]"></vl-style-icon>
          </vl-style-box>
      </vl-feature>
      <!--// overlay marker -->



      <!-- base layers -->
      <vl-layer-tile v-for="layer in baseLayers" :key="layer.name" :id="layer.name" :visible="layer.visible">
        <component :is="'vl-source-' + layer.name" v-bind="layer"></component>
      </vl-layer-tile>
      <!--// base layers -->
    </vl-map>
    <!--// app map -->

    <!-- map panel, controls -->
    <div class="map-panel">
      <b-collapse class="panel box is-paddingless" :open.sync="panelOpen">
        <div slot="trigger" class="panel-heading">
          <div class="columns">
            <div class="column is-11">
              <strong>Map panel</strong>
            </div>
            <div class="column">
              <b-icon :icon="panelOpen ? 'chevron-up' : 'chevron-down'" size="is-small"></b-icon>
            </div>
          </div>
        </div>
        <p class="panel-tabs">
          <a @click="showMapPanelTab('state')" :class="mapPanelTabClasses('state')">State</a>
          <a @click="showMapPanelTab('town')" :class="mapPanelTabClasses('town')">Town</a>
        </p>

        <div class="panel-block" v-show="mapPanel.tab === 'state'">
          <table class="table is-fullwidth">
            <tr>
              <th>Map center</th>
              <td>{{ center }}</td>
            </tr>
            <tr>
              <th>Map zoom</th>
              <td>{{ zoom }}</td>
            </tr>
            <tr>
              <th>Click coordinates</th>
              <td>{{ clickCoordinate }}</td>
            </tr>
            <tr>
              <th>Selected features</th>
              <td>{{ selectedFeatures.map(f => f.id) }}</td>
            </tr>
          </table>
        </div>

        <div class="panel-block" v-show="mapPanel.tab === 'town'">
          <table class="table is-fullwidth">
            <tr v-for="item in items" :key="item.key">
              <th>{{item.name}}</th>
            </tr>
          </table>
        </div>
      </b-collapse>
    </div>
    <!--// map panel, controls -->

    <!-- base layers switch -->
    <div class="base-layers-panel">
      <div class="buttons has-addons">
        <button class="button is-light" v-for="layer in baseLayers"
                :key="layer.name" :class="{ 'is-info': layer.visible }"
                @click="showBaseLayer(layer.name)">
          {{ layer.title }}
        </button>
        <button class="button is-light" @click="mapVisible = !mapVisible">
          {{ mapVisible ? 'Hide map' : 'Show map' }}
        </button>
      </div>
    </div>
    <!--// base layers -->
  </div>
</template>

<script>
  import { findPointOnSurface } from 'vuelayers/lib/ol-ext'
  import ScaleLine from 'ol/control/ScaleLine'
  import FullScreen from 'ol/control/FullScreen'
  import OverviewMap from 'ol/control/OverviewMap'
  import ZoomSlider from 'ol/control/ZoomSlider'
  import axios from 'axios'

  const methods = {
    pointOnSurface: findPointOnSurface,
    onMapMounted () {
      // now ol.Map instance is ready and we can work with it directly
      this.$refs.map.$map.getControls().extend([
        new ScaleLine(),
        new FullScreen(),
        new OverviewMap({
          collapsed: false,
          collapsible: true,
        }),
        new ZoomSlider(),
      ])
    },
    // base layers
    showBaseLayer (name) {
      let layer = this.baseLayers.find(layer => layer.visible)
      if (layer != null) {
        layer.visible = false
      }

      layer = this.baseLayers.find(layer => layer.name === name)
      if (layer != null) {
        layer.visible = true
      }
    },
    // map panel
    mapPanelTabClasses (tab) {
      return {
        'is-active': this.mapPanel.tab === tab,
      }
    },
    showMapPanelLayer (layer) {
      layer.visible = !layer.visible
    },
    showMapPanelTab (tab) {
      this.mapPanel.tab = tab
      if (tab !== 'draw') {
        this.drawType = undefined
      }
    },
  }

  export default {
    name: 'vld-demo-app',
    methods,

    mounted () {
      axios
        .get('http://localhost:8080/api/town')
        .then(response => (this.items = response.data))
    },

    data () {
      return {
        center: [4.895976, 45.756169],
        zoom: 13,
        rotation: 0,
        clickCoordinate: undefined,
        selectedFeatures: [],
        mapPanel: {
          tab: 'state',
        },
        // Fetch item
        items: [],
        panelOpen: true,
        mapVisible: true,
        // base layers
        baseLayers: [
          {
            name: 'osm',
            title: 'OpenStreetMap',
            visible: true,
          },
          {
            name: 'sputnik',
            title: 'Sputnik Maps',
            visible: false,
          },
          {
            name: 'bingmaps',
            title: 'Bing Maps',
            apiKey: 'ArbsA9NX-AZmebC6VyXAnDqjXk6mo2wGCmeYM8EwyDaxKfQhUYyk0jtx6hX5fpMn',
            imagerySet: 'CanvasGray',
            visible: false,
          },
        ],

      }
    },
  }
</script>

<style lang="sass">
  @import ~bulma/sass/utilities/_all

  html, body, #app
    width: 100%
    height: 100%
    margin: 0
    padding: 0

  .vld-demo-app
    position: relative

    .map
      height: 100%
      width: 100%

    .map-panel
      padding: 0
      width: 27em !important

      .panel-heading
        box-shadow: 0 .25em .5em transparentize($dark, 0.8)

      .panel-content
        background: $white
        box-shadow: 0 .25em .5em transparentize($dark, 0.8)

      .panel-block
        &.draw-panel
          .buttons
            .button
              display: block
              flex: 1 1 100%

      +tablet()
        position: absolute
        top: 0
        right: 0
        max-height: 500px
        width: 22em

    .base-layers-panel
      position: absolute
      left: 50%
      bottom: 20px
      transform: translateX(-50%)

    .feature-popup
      position: absolute
      left: -33px
      bottom: 12px
      width: 20em
      max-width: none

      &:after, &:before
        top: 100%
        border: solid transparent
        content: ' '
        height: 0
        width: 0
        position: absolute
        pointer-events: none
      &:after
        border-top-color: white
        border-width: 10px
        left: 48px
        margin-left: -10px
      &:before
        border-top-color: #cccccc
        border-width: 11px
        left: 48px
        margin-left: -11px

      .card-content
        max-height: 20em
        overflow: auto

      .content
        word-break: break-all
</style>

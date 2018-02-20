import React from 'react';
import Native from 'react-native';
import { StyleSheet, Text, View } from 'react-native';
import { rootelement } from 'shadow-cljs/com.wsscode.multi_timer.core';

export default class App extends React.Component {
  render() {
    return rootelement();
  }
}

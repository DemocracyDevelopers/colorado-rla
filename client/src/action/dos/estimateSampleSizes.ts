import { endpoint } from 'corla/config';

export default () => {
    const url = `${endpoint('estimate-sample-sizes')}`;

    window.location.replace(url);
};
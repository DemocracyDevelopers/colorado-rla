import { endpoint } from 'corla/config';

export default () => {
    const url = `${endpoint('export-assertions')}`;

    window.location.replace(url);
};
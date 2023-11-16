import { endpoint } from 'corla/config';

import action from 'corla/action';
import {empty} from 'corla/util';

const url = endpoint('generate-assertions');

async function generateAssertions() {
    try {
            action('GENERATE_ASSERTIONS_SEND', { });

            const r = await fetch(url, {});

            const received = await r.json().catch(empty);

            if (!r.ok) {
                    action('GENERATE_ASSERTIONS_FAIL', { });
                    return false;
            }

            action('GENERATE_ASSERTIONS_OK', {  });
            return true;

    } catch (e) {
            action('GENERATE_ASSERTIONS_FAIL', {  });

            throw e;
    }
}

export default generateAssertions;

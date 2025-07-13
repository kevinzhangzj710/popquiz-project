import createFetchClient from 'openapi-fetch';
import createClient from 'openapi-react-query';
import type {paths} from "./api-schema";

const baseUrl = '/'

const fetchClient = createFetchClient<paths>({
    baseUrl
})

export const $api = createClient(fetchClient);

export const $fetch = fetchClient;
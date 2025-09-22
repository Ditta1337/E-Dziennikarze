export const prepareUserData = (user) => {
    const addressParts = user.address.split(';');
    return {
        ...user,
        phone: user.contact,
        address: addressParts[0],
        address_code: addressParts[1],
        city: addressParts[2],
        country: addressParts[3],
    };
}